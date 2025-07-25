package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.entity.ReviewImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


public class ReviewImageEntityUnitTest {
    private Book book;
    private Review review;
    private final String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";


    @BeforeEach
    void setUp() {
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

        review = new Review(
                999L,
                "리뷰리뷰리뷰",
                "포인트냠냠",
                3,
                LocalDateTime.of(2024, 2, 19, 21, 32, 11),
                book,
                77L
        );
    }

    @Test
    @DisplayName("ReviewImage Entity of 메서드 테스트")
    void entityOfMethodTest() {
        // given & when
        ReviewImage image = ReviewImage.of(imageUrl, review);

        // then
        assertThat(image.getImageId()).isNull();
        assertThat(image.getImageUrl()).isEqualTo("https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif");
        assertThat(image.getReview()).isEqualTo(review);
    }
}
