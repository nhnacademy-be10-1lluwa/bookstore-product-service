package com.nhnacademy.illuwa.d_review.review.service;

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
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MinioStorageService minioStorageService;

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

        if(images != null) {
            for(MultipartFile image : images){
                String uploadedUrl = minioStorageService.uploadFile("review", memberId, image);
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, review);
                reviewImageRepository.save(reviewImage);
            }
        }

        // TODO: 포인트 적립 로직 추후 추가
        // API 요청: /members/{memberId}/points/event?reason=REVIEW 또는 PHOTO_REVIEW
        return ReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewPages(Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findReviewsByBook_Id(bookId, pageable);

        return reviews.map(ReviewResponse::from);
    }

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
        List<String> updateImages = request.getReviewImageUrls();

        for(ReviewImage image : existingImages){
            if(!updateImages.contains(image.getImageUrl())){
                reviewImageRepository.delete(image);
                minioStorageService.deleteFile(image.getImageUrl());
            }
        }

        if(images != null) {
            for(MultipartFile newImage : images){
                String uploadedUrl = minioStorageService.uploadFile("review", memberId, newImage);
                ReviewImage reviewImage = ReviewImage.of(uploadedUrl, review);
                reviewImageRepository.save(reviewImage);
            }
        }

        return ReviewResponse.from(review);
    }
}