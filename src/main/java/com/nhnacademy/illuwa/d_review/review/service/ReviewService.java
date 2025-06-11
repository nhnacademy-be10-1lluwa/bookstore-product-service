package com.nhnacademy.illuwa.d_review.review.service;

import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewResponse createReview(Long bookId, ReviewRequest request) {
        // TODO: memberId 가져오는 방식 확정되면 변경
        Long memberId = null;

        String imageUrl = null;
        if (!request.getReviewImageUrl().isBlank()) {
            imageUrl = request.getReviewImageUrl();
        }

        Review review = Review.of(request.getReviewTitle(),
                request.getReviewContent(),
                imageUrl,
                request.getReviewRating(),
                request.getReviewDate(),
                bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("temp")/*BookNotFoundException(bookId)*/),
                memberId);

        Review save = reviewRepository.save(review);
        return new ReviewResponse(save.getReviewId(),
                save.getReviewTitle(),
                save.getReviewContent(),
                save.getReviewImageUrl(),
                save.getReviewRating(),
                save.getReviewDate(),
                save.getBook().getId(),
                save.getMemberId());
    }

    public List<ReviewResponse> getReviewList(Long bookId) {
        List<Review> reviewList = reviewRepository.findReviewsByBook_Id(bookId);
        List<ReviewResponse> reviewResponseList = new ArrayList<>();
        for (Review review : reviewList) {
            reviewResponseList.add(
                    new ReviewResponse(review.getReviewId(),
                            review.getReviewTitle(),
                            review.getReviewContent(),
                            review.getReviewImageUrl(),
                            review.getReviewRating(),
                            review.getReviewDate(),
                            review.getBook().getId(),
                            review.getMemberId()
                    )
            );
        }

        return reviewResponseList;
    }

    public ReviewResponse getReviewDetail(Long bookId, Long reviewId) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId).orElseThrow(ReviewNotFoundException::new);
        return new ReviewResponse(review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getReviewImageUrl(),
                review.getReviewRating(),
                review.getReviewDate(),
                review.getBook().getId(),
                review.getMemberId());
    }
}