package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class ReviewEntityUnitTest {
    private Book book;

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
    @DisplayName("Review Entity of 메서드 테스트")
    void entityOfMethodTest() {
        // given
        Long memberId = 99L;

        String title = "인생을 뒤바꾼 책";
        String content = "이 책을 읽고 내인생이 달라졌다~";
        Integer rating = 5;
        LocalDateTime created = LocalDateTime.now();

        // when
        Review review = Review.of(title, content, rating, created, book, memberId);

        // then
        assertThat(review.getReviewTitle()).isEqualTo("인생을 뒤바꾼 책");
        assertThat(review.getReviewContent()).isEqualTo("이 책을 읽고 내인생이 달라졌다~");
        assertThat(review.getReviewRating()).isEqualTo(5);
        assertThat(review.getReviewDate()).isEqualTo(created);
        assertThat(review.getBook().getId()).isEqualTo(999999L);
        assertThat(review.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Review Entity update 메서드 테스트")
    void entityUpdateMethodTest(){
        // given
        Review review = Review.of(
                "낫배드",
                "하지만 그게 다임",
                2,
                LocalDateTime.of(2025, 7, 19, 21, 32, 11),
                book,
                99L
        );

        // when
        String newTitle = "Not Bad";
        String newContent = "But, That's all, No more.";
        Integer newRating = 3;
        review.update(newTitle, newContent, newRating);

        // then
        assertThat(review.getReviewTitle()).isEqualTo("Not Bad");
        assertThat(review.getReviewContent()).isEqualTo("But, That's all, No more.");
        assertThat(review.getReviewRating()).isEqualTo(3);

        // when 2
        newTitle = null;
        newContent = null;
        newRating = null;
        review.update(newTitle, newContent, newRating);

        // then 2
        assertThat(review.getReviewTitle()).isEqualTo("Not Bad");
        assertThat(review.getReviewContent()).isEqualTo("But, That's all, No more.");
        assertThat(review.getReviewRating()).isEqualTo(3);
    }
}
