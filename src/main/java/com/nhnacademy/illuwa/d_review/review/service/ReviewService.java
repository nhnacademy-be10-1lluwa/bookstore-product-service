package com.nhnacademy.illuwa.d_review.review.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewImageRepository;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewRequest request, Long memberId) {
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
        // TODO: 포인트 적립 로직 추후 추가
        return ReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewPages(Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findReviewsByBook_Id(bookId, pageable);

        return reviews.map(ReviewResponse::from);
    }

    @Transactional
    public ReviewResponse updateReview(Long bookId, Long reviewId, ReviewRequest request, Long memberId) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        if(!Objects.equals(memberId, review.getMemberId())){
            throw new ReviewNotFoundException(reviewId);
        }

        review.update(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating()
        );

        return ReviewResponse.from(review);
    }
}