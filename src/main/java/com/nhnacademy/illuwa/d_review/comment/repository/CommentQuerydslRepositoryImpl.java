package com.nhnacademy.illuwa.d_review.comment.repository;

import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.comment.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentQuerydslRepositoryImpl implements CommentQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public void updateComment(Comment comment) {
        QComment qComment = QComment.comment;

        queryFactory.update(qComment)
                    .set(qComment.commentContents, comment.getCommentContents())
                    .where(
                        qComment.review.reviewId.eq(comment.getReview().getReviewId()),
                        qComment.commentId.eq(comment.getCommentId()),
                        qComment.memberId.eq(comment.getMemberId())
                    )
                    .execute();
    }
}
