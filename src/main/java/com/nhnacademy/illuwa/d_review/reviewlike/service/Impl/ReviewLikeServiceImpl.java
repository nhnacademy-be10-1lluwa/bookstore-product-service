package com.nhnacademy.illuwa.d_review.reviewlike.service.Impl;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import com.nhnacademy.illuwa.d_review.reviewlike.exception.AlreadyLikedException;
import com.nhnacademy.illuwa.d_review.reviewlike.exception.CannotCancelLikeException;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.ReviewLikeRepository;
import com.nhnacademy.illuwa.d_review.reviewlike.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewLikeResponse addLike(Long bookId, Long reviewId, Long memberId) {
        // TODO: 여기서 예외가 진짜 필요할까??
        if(isLikedByMe(reviewId, memberId)) {
            throw new AlreadyLikedException();
        }

        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        reviewLikeRepository.save(ReviewLike.of(review, memberId));

        return ReviewLikeResponse.from(isLikedByMe(reviewId, memberId), getLikeCount(reviewId));
    }

    @Override
    @Transactional
    public ReviewLikeResponse cancelLike(Long bookId, Long reviewId, Long memberId) {
        if(!isLikedByMe(reviewId, memberId)) {
            // TODO: 여기서 예외가 진짜 필요할까??
            throw new CannotCancelLikeException();
        }

        reviewLikeRepository.deleteByReview_ReviewIdAndMemberId(reviewId, memberId);

        return ReviewLikeResponse.from(isLikedByMe(reviewId, memberId), getLikeCount(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewLikeResponse getLikeInfo(Long bookId, Long reviewId, Long memberId) {
        return ReviewLikeResponse.from(isLikedByMe(reviewId, memberId), getLikeCount(reviewId));
    }

    @Override
    public boolean isLikedByMe(Long reviewId, Long memberId) {
        return reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    public long getLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReview_ReviewId(reviewId);
    }
}
