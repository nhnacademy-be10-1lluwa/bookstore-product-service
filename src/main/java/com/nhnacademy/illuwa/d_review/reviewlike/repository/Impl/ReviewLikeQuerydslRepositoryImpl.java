package com.nhnacademy.illuwa.d_review.reviewlike.repository.Impl;

import com.nhnacademy.illuwa.d_review.reviewlike.entity.QReviewLike;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.ReviewLikeQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewLikeQuerydslRepositoryImpl implements ReviewLikeQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    QReviewLike reviewLike = QReviewLike.reviewLike;

    @Override
    public List<Long> findMyLikedReviewIds(List<Long> reviewIds, Long memberId) {
        return queryFactory
                .select(reviewLike.review.reviewId)
                .from(reviewLike)
                .where(
                        reviewLike.review.reviewId.in(reviewIds),
                        reviewLike.memberId.eq(memberId)
                )
                .fetch();
    }

    @Override
    public Map<Long, Long> countLikesByReviewIds(List<Long> reviewIds) {
        return queryFactory
                .select(reviewLike.review.reviewId, reviewLike.count())
                .from(reviewLike)
                .where(reviewLike.review.reviewId.in(reviewIds))
                .groupBy(reviewLike.review.reviewId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(reviewLike.review.reviewId),
                        tuple -> tuple.get(reviewLike.count()) // 실제 DB에서 null 안나옴
                ));
    }
}
