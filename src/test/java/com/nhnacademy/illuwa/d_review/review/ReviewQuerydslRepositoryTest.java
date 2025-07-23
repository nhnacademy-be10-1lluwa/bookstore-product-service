package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.repository.Impl.ReviewQuerydslRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class ReviewQuerydslRepositoryTest {

    @Autowired
    private ReviewQuerydslRepositoryImpl reviewQuerydslRepository;

    @Autowired
    private TestEntityManager em;

    Book book;
    Review review;

    @BeforeEach
    void setUp() {
        String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";
        book = new Book(
                null,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(2024, 1, 1),
                "123456789X",
                BigDecimal.valueOf(100000L),
                BigDecimal.valueOf(99999L),
                new ArrayList<>(),
                new BookExtraInfo(Status.NORMAL, false, 1),
                null,
                null
        );
        em.persist(book);

        BookImage bookImage = new BookImage(book, imageUrl, ImageType.DETAIL);
        book.getBookImages().add(bookImage);
        em.persist(bookImage);

        review = new Review(
                null,
                "리뷰 제목",
                "리뷰 내용",
                5,
                LocalDateTime.of(2025, 1, 1, 12, 40, 20),
                book,
                99L
        );
        em.persist(review);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("리뷰 ID 리스트로 책 제목 Map 조회 - 성공")
    void testFindBookTitleMapByReviewIds() {
        // given
        List<Long> reviewIds = List.of(review.getReviewId());

        // when
        Map<Long, String> result = reviewQuerydslRepository.findBookTitleMapByReviewIds(reviewIds);

        // then
        assertEquals(1, result.size());
        assertEquals("이상한 책", result.get(review.getReviewId()));
    }

    @Test
    @DisplayName("리뷰 ID 리스트가 비어있을 때 빈 Map 반환")
    void testWhenEmptyIds_thenReturnEmptyMap() {
        // given
        List<Long> emptyList = Collections.emptyList();

        // when
        Map<Long, String> result = reviewQuerydslRepository.findBookTitleMapByReviewIds(emptyList);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("null 이 전달됐을 때 빈 Map 반환")
    void testWhenNull_thenReturnEmptyMap() {
        // when
        Map<Long, String> result = reviewQuerydslRepository.findBookTitleMapByReviewIds(null);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("책 제목이 공백일 경우 '제목 없음' 반환")
    void testWhenTitleNull_thenReturnsDefaultMessage() {
        // given
        Book noTitleBook = new Book(
                null,
                "",
                "목차없음",
                "아무말",
                "수상한 사람2",
                "무명출판",
                LocalDate.of(2025, 1, 1),
                "123456789Y",
                BigDecimal.valueOf(100000L),
                BigDecimal.valueOf(99999L),
                new ArrayList<>(),
                new BookExtraInfo(Status.NORMAL, false, 1),
                null,
                null
        );
        em.persist(noTitleBook);

        Review review2 = new Review(
                null,
                "리뷰 제목2",
                "리뷰 내용2",
                4,
                LocalDateTime.now(),
                noTitleBook,
                100L
        );
        em.persist(review2);
        em.flush();
        em.clear();

        List<Long> reviewIds = List.of(review2.getReviewId());

        // when
        Map<Long, String> result = reviewQuerydslRepository.findBookTitleMapByReviewIds(reviewIds);

        // then
        assertEquals(1, result.size());
        assertEquals("제목 없음", result.get(review2.getReviewId()));
    }
}