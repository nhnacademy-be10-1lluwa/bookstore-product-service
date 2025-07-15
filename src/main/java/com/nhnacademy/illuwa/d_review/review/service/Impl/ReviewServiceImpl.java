package com.nhnacademy.illuwa.d_review.review.service.Impl;

import com.nhnacademy.illuwa.common.client.member.MemberServiceClient;
import com.nhnacademy.illuwa.common.client.order.OrderServiceClient;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.entity.ReviewImage;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.CannotWriteReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewImageRepository;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.ReviewLikeRepository;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final MinioStorageService minioStorageService;
    private final MemberServiceClient memberServiceClient;
    private final OrderServiceClient orderServiceClient;

    @Override
    @Transactional
    public ReviewResponse createReview(Long bookId, Long memberId, ReviewRequest request){
//        if(Boolean.FALSE.equals(orderServiceClient.isConfirmedOrder(memberId, bookId).getBody())){
//            throw new CannotWriteReviewException("구매가 확정되지 않아서 리뷰를 작성하실 수 없습니다!");
//        }
        if(reviewRepository.findByBook_IdAndMemberId(bookId, memberId).isPresent()){
            throw new CannotWriteReviewException("리뷰 중복 작성은 불가능합니다!");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException("리뷰를 작성할 도서를 찾을 수 없습니다."));

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating(),
                LocalDateTime.now(),
                book,
                memberId
        );
        Review saved = reviewRepository.save(review);

        String rewardType = "REVIEW";
        List<String> imageUrls = new ArrayList<>();
        List<MultipartFile> images = request.getImages();
        if(!images.isEmpty() && !Objects.requireNonNull(images.getFirst().getOriginalFilename()).isBlank()) {
            for(MultipartFile image : images){
                String uploadedUrl = minioStorageService.uploadReviewImage(memberId, image).getUrl();
                imageUrls.add(uploadedUrl);
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, saved);
                reviewImageRepository.save(reviewImage);
            }
            rewardType = "PHOTO_REVIEW";
        }

        memberServiceClient.earnEventPoint(memberId, rewardType);

        return ReviewResponse.from(saved, imageUrls, false, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewPages(Long bookId, Pageable pageable, Long memberId) {
        Page<Review> reviews = reviewRepository.findReviewsByBook_Id(bookId, pageable);
        List<Long> reviewIds = reviews.getContent().stream()
                .map(Review::getReviewId)
                .toList();

        // 1. 좋아요 상태 (내가 누른 리뷰 ID)
        Set<Long> likedReviewIds = new HashSet<>(
                reviewLikeRepository.findMyLikedReviewIds(reviewIds, memberId)
        );

        // 2. 좋아요 수
        Map<Long, Long> likeCountMap = reviewLikeRepository.countLikesByReviewIds(reviewIds);

        // 3. 이미지
        List<ReviewImage> allImages = reviewImageRepository.findAllByReview_ReviewIdIn(reviewIds);
        Map<Long, List<String>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(
                        image -> image.getReview().getReviewId(),
                        Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())
                ));

        // 4. 최종 변환
        return reviews.map(review -> {
            boolean likedByMe = likedReviewIds.contains(review.getReviewId());
            long likeCount = likeCountMap.getOrDefault(review.getReviewId(), 0L);
            List<String> imageUrls = imageMap.getOrDefault(review.getReviewId(), List.of());

            return ReviewResponse.from(review, imageUrls, likedByMe, likeCount);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewDetails(Long bookId, Long reviewId, Long memberId) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));

        List<String> imageUrls = reviewImageRepository.findAllByReview_ReviewId(reviewId).stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        boolean likedByMe = reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId);
        long likeCount = reviewLikeRepository.countByReview_ReviewId(reviewId);

        return ReviewResponse.from(review, imageUrls, likedByMe, likeCount);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long bookId, Long reviewId, Long memberId, ReviewRequest request) throws Exception {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));
        if(!Objects.equals(memberId, review.getMemberId())){
            throw new MemberIdDoesNotMatchWithReviewException("해당글의 작성자가 아닙니다. 현재 유저 ID: " + memberId + " 글 작성자 ID: " + review.getMemberId());
        }

        review.update(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating()
        );

        List<MultipartFile> newImages = request.getImages();
        // 새 이미지 업로드
        if(!newImages.isEmpty() && !Objects.requireNonNull(newImages.getFirst().getOriginalFilename()).isBlank()) {
            for (MultipartFile newImage : newImages) {
                String uploadedUrl = minioStorageService.uploadReviewImage(memberId, newImage).getUrl();
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, review);
                reviewImageRepository.save(reviewImage);
            }
        }

        // 이미지 삭제
        List<ReviewImage> existingImages = reviewImageRepository.findAllByReview_ReviewId(reviewId);
        Set<String> deleteImageSet = request.getDeleteImageUrls() == null ? Set.of() : new HashSet<>(request.getDeleteImageUrls());

        for (ReviewImage image : existingImages) {
            if (deleteImageSet.contains(image.getImageUrl())) {
                reviewImageRepository.delete(image);
                String path = URI.create(image.getImageUrl()).getPath();
                String storageName = path.substring(path.lastIndexOf('/') + 1);
                minioStorageService.deleteFile(storageName);
            }
        }

        List<String> imageUrls = reviewImageRepository.findAllByReview_ReviewId(reviewId)
                .stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        boolean likedByMe = reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId);
        long likeCount = reviewLikeRepository.countByReview_ReviewId(reviewId);

        return ReviewResponse.from(review, imageUrls, likedByMe, likeCount);
    }
}
