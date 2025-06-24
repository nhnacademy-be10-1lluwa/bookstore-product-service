package com.nhnacademy.illuwa.d_review.reviewlike.service.Impl;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
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
    public ReviewLikeResponse toggleLike(Long bookId, Long reviewId, Long memberId) {
        Review review = reviewRepository.findByBook_IdAndReviewId(bookId, reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        boolean isLikedByMe = isLikedByMe(reviewId, memberId); // 중복호출 막으려고 로컬변수 만듬

        if(isLikedByMe) {
            reviewLikeRepository.deleteByReview_ReviewIdAndMemberId(reviewId, memberId);
        } else{
            reviewLikeRepository.save(ReviewLike.of(review, memberId));
        }

        return new ReviewLikeResponse(!isLikedByMe, getLikeCount(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewLikeResponse getLikeInfo(Long reviewId, Long memberId) {
        return new ReviewLikeResponse(isLikedByMe(reviewId, memberId), getLikeCount(reviewId));
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
