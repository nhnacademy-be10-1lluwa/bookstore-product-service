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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewLikeResponse toggleLike(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));

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
    public Map<Long, Long> getLikeCountsFromReviews(List<Long> reviewIds) {
        return reviewLikeRepository.countLikesByReviewIds(reviewIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMyLikedReviews(List<Long> reviewIds, Long memberId) {
        return reviewLikeRepository.findMyLikedReviewIds(reviewIds, memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByMe(Long reviewId, Long memberId) {
        return reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReview_ReviewId(reviewId);
    }
}
