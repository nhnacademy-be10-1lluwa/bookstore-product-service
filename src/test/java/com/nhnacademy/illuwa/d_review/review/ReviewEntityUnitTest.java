package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class ReviewEntityUnitTest {
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
                "book.jpg"
        );
    }

    @Test
    @DisplayName("Review Entity of 메서드 테스트")
    void entityOfMethodTest() {
        // given
        Long memberId = 99L;

        String title = "인생을 뒤바꾼 책";
        String content = "대가리가 깨져도 추천";
        String imageUrl = "book.jpg";
        Integer rating = 5;
        LocalDateTime created = LocalDateTime.now();

        // when
        Review review = Review.of(title, content, imageUrl, rating, created, book, memberId);

        // then
        assertThat(review.getReviewTitle()).isEqualTo("인생을 뒤바꾼 책");
        assertThat(review.getReviewContent()).isEqualTo("대가리가 깨져도 추천");
        assertThat(review.getReviewImageUrl()).isEqualTo("book.jpg");
        assertThat(review.getReviewRating()).isEqualTo(5);
        assertThat(review.getReviewDate()).isEqualTo(created);
        assertThat(review.getBook().getId()).isEqualTo(10L);
        assertThat(review.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Review Entity update 메서드 테스트")
    void entityUpdateMethodTest(){
        // given
        Review review = Review.of(
                "낫배드",
                "하지만 그 뿐",
                "book.jpg",
                2,
                LocalDateTime.now(),
                book,
                99L
        );

        // when
        String newTitle = "Not Bad";
        String newContent = "But, That's all, No more.";
        String newImageUrl = "new_book.jpg";
        Integer newRating = 3;
        review.update(newTitle, newContent, newImageUrl, newRating);

        // then
        assertThat(review.getReviewTitle()).isEqualTo("Not Bad");
        assertThat(review.getReviewContent()).isEqualTo("But, That's all, No more.");
        assertThat(review.getReviewImageUrl()).isEqualTo("new_book.jpg");
        assertThat(review.getReviewRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("Review Entity update 메서드 테스트 - Null or Blank")
    void entityUpdateWithNullTest(){
        // given
        Review review = Review.of(
                "낫배드",
                "하지만 그 뿐",
                "book.jpg",
                2,
                LocalDateTime.now(),
                book,
                99L
        );

        // when
        review.update(null, null, " ", null);

        // then
        assertThat(review.getReviewTitle()).isEqualTo("낫배드");
        assertThat(review.getReviewContent()).isEqualTo("하지만 그 뿐");
        assertThat(review.getReviewImageUrl()).isEqualTo("book.jpg");
        assertThat(review.getReviewRating()).isEqualTo(2);

        // when2
        review.update(null, null, null, null);

        // then2
        assertThat(review.getReviewImageUrl()).isEqualTo("book.jpg");
    }
}
