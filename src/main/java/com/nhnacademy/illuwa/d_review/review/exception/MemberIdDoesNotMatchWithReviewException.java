package com.nhnacademy.illuwa.d_review.review.exception;

public class MemberIdDoesNotMatchWithReviewException extends RuntimeException {
    public MemberIdDoesNotMatchWithReviewException(Long memberId, Long reviewMemberId) {
        super("해당글의 작성자가 아닙니다. 현재 유저 ID: " + memberId + " 글 작성자 ID: " + reviewMemberId);
    }
}
