package com.nhnacademy.illuwa.d_review.review.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewListResponse;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewResponse createReview(Long bookId, ReviewRequest request) {
        // TODO: memberId 가져오는 방식 확정되면 변경
        Long memberId = null;

        String imageUrl = (request.getReviewImageUrl() != null && !request.getReviewImageUrl().isBlank())
                ? request.getReviewImageUrl()
                : null;

        // TODO: BookNotFoundException 작성되면 변경
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException("도서를 찾을 수 없습니다."));

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                imageUrl,
                request.getReviewRating(),
                LocalDateTime.now(),
                book,
                memberId
        );

        Review saved = reviewRepository.save(review);
        // TODO: 포인트 적립 로직 추후 추가
        return ReviewResponse.from(saved);
    }

    public ReviewListResponse getReviewList(Long bookId) {
        List<Review> reviews = reviewRepository.findReviewsByBook_Id(bookId);
        List<ReviewResponse> reviewResponseList = reviews.stream().map(ReviewResponse::from).toList();
        return new ReviewListResponse(reviewResponseList);
    }

    public ReviewResponse getReviewDetail(Long bookId, Long reviewId) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        return ReviewResponse.from(review);
    }

    public ReviewResponse updateReview(Long bookId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.update(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewImageUrl(),
                request.getReviewRating()
        );

        return ReviewResponse.from(review);
    }
}