package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewDtoUnitTest {
    private Book book;
    private final LocalDateTime fixedDateTime =LocalDateTime.of(2025, 1, 1, 14, 30, 31);

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("ReviewResponse From 메서드 테스트")
    void responseFromMethodTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Review review = Review.of(
                "title",
                "content",
                5,
                fixedDateTime,
                book,
                99L
        );

        // when
        ReviewResponse response = ReviewResponse.from(review, null);

        // then
        assertThat(response.getReviewTitle()).isEqualTo("title");
        assertThat(response.getReviewContent()).isEqualTo("content");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(fixedDateTime);
        assertThat(response.getBookId()).isEqualTo(999999L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }
}