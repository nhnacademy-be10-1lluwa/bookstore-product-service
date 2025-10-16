package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewLikeEntityUnitTest {
    private Book book;
    private final LocalDateTime fixedTime = LocalDateTime.of(2099, 8, 22, 4, 17, 32);
    @Test
    @DisplayName("ReviewLike Entity of 메서드 테스트")
    void entityOfMethodTest(){
        //given
        String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";
        book = new Book(
                999999L,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(2024, 1, 1),
                "123456789X",
                BigDecimal.valueOf(100000L),
                BigDecimal.valueOf(99999L),
                List.of(new BookImage(book, imageUrl, ImageType.DETAIL)),
                new BookExtraInfo(Status.NORMAL,false,1),
                null,
                null
        );

        Review review = new Review(
                1L,
                "리뷰 제목",
                "리뷰 내용",
                5,
                fixedTime,
                book,
                9999L
        );
        Long memberId = 999L;

        // when
        ReviewLike reviewLike = ReviewLike.of(review, memberId);

        // then
        assertThat(reviewLike.getLikeId()).isNull();
        assertThat(reviewLike.getReview().getReviewId()).isEqualTo(1L);
        assertThat(reviewLike.getMemberId()).isEqualTo(999L);
    }
}