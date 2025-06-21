package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewLikeEntityUnitTest {
    @Test
    @DisplayName("ReviewLike Entity of 메서드 테스트")
    void entityOfMethodTest(){
        //given
        Book book = new Book(
                10L,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.now().minusYears(1L),
                "11111111111111111",
                100,
                100,
                null,
                new BookExtraInfo(Status.DELETED,true)
        );

        Review review = new Review(
                4L,
                "리뷰임",
                "내용임",
                "/img/review.png",
                3,
                LocalDateTime.of(2025, 6, 20, 13, 20),
                book,
                99L
        );
        Long memberId = 999L;

        // when
        ReviewLike reviewLike = ReviewLike.of(review, memberId);

        // then
        assertThat(reviewLike.getReview().getReviewId()).isEqualTo(4L);
        assertThat(reviewLike.getMemberId()).isEqualTo(999L);
    }
}