package com.nhnacademy.illuwa.d_review.review.repository.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.QBook;
import com.nhnacademy.illuwa.d_review.review.entity.QReview;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewQuerydslRepositoryImpl implements ReviewQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    QReview review = QReview.review;
    QBook book = QBook.book;

    @Override
    public Map<Long,String> findBookTitleMapByReviewIds(Collection<Long> reviewIds){
        if (reviewIds == null || reviewIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return queryFactory
                .select(review.reviewId, book.title)
                .from(review)
                .join(review.book, book)
                .where(review.reviewId.in(reviewIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(review.reviewId),
                        tuple -> {
                            String title = tuple.get(book.title);
                            return Objects.requireNonNull(title).isBlank() ? "제목 없음" : title;
                        }
                ));
    }
}