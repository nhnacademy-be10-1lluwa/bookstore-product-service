package com.nhnacademy.illuwa.d_review.review.service.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.entity.ReviewImage;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
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

    @Override
    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewRequest request, Long memberId, List<MultipartFile> images) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException("도서를 찾을 수 없습니다."));

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating(),
                LocalDateTime.now(),
                book,
                memberId
        );
        Review saved = reviewRepository.save(review);

        List<String> imageUrls = new ArrayList<>();
        if(images != null) {
            for(MultipartFile image : images){
                String uploadedUrl = minioStorageService.uploadFile("review", memberId, image);
                imageUrls.add(uploadedUrl);
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, saved);
                reviewImageRepository.save(reviewImage);
            }
        }

        // TODO: 포인트 적립 로직 추후 추가
        // feign client API 요청: /members/{memberId}/points/event?reason=REVIEW 또는 PHOTO_REVIEW

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
    @Transactional
    public ReviewResponse updateReview(Long bookId, Long reviewId, ReviewRequest request, Long memberId, List<MultipartFile> images) throws Exception {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));
        if(!Objects.equals(memberId, review.getMemberId())){
            throw new MemberIdDoesNotMatchWithReviewException("해당글의 작성자가 아닙니다. 현재 유저 ID: " + memberId + " 글 작성자 ID: " + review.getMemberId());
        }

        review.update(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating()
        );

        List<ReviewImage> existingImages = reviewImageRepository.findAllByReview_ReviewId(reviewId);
        List<String> updateImages = new ArrayList<>();
        if(images != null) {
            for(MultipartFile newImage : images){
                String uploadedUrl = minioStorageService.uploadFile("review", memberId, newImage);
                updateImages.add(uploadedUrl);
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, review);
                reviewImageRepository.save(reviewImage);
            }
        }

        for(ReviewImage image : existingImages){
            if(!updateImages.contains(image.getImageUrl())){
                reviewImageRepository.delete(image);
                minioStorageService.deleteFile(image.getImageUrl());
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
