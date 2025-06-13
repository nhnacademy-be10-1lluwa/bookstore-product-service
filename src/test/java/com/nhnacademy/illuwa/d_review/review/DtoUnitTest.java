package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class DtoUnitTest {
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(
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
                false,
                "book.jpg",
                "카테고리없음"
        );
    }

    @Test
    @DisplayName("ReviewResponse From 메서드 테스트")
    void responseFromMethodTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Review review = Review.of( "title",
                "content",
                "img.jpg",
                5,
                now,
                book,
                99L
        );

        // when
        ReviewResponse response = ReviewResponse.from(review);

        // then
        assertThat(response.getReviewTitle()).isEqualTo("title");
        assertThat(response.getReviewContent()).isEqualTo("content");
        assertThat(response.getReviewImageUrl()).isEqualTo("img.jpg");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }
}