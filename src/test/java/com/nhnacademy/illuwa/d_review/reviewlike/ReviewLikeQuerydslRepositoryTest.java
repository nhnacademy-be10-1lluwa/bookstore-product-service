package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.Impl.ReviewLikeQuerydslRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class ReviewLikeQuerydslRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ReviewLikeQuerydslRepositoryImpl querydslRepository;

    private Review review;

    @BeforeEach
    void setUp() {
        String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";

        Book book = new Book(
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
    void findMyLikedReviewIds_returnsEmptyList_whenNoLikes() {
        List<Long> reviewIds = List.of(review.getReviewId());
        Long memberId = 123L;

        List<Long> likedReviewIds = querydslRepository.findMyLikedReviewIds(reviewIds, memberId);

        assertThat(likedReviewIds).isEmpty();
    }

    @Test
    void findMyLikedReviewIds_returnsCorrectReviewIds() {
        Long memberId = 123L;
        ReviewLike reviewLike = ReviewLike.of(review, memberId);
        em.persist(reviewLike);

        em.flush();
        em.clear();

        List<Long> reviewIds = List.of(review.getReviewId());

        List<Long> likedReviewIds = querydslRepository.findMyLikedReviewIds(reviewIds, memberId);

        assertThat(likedReviewIds).containsExactly(review.getReviewId());
    }

    @Test
    void countLikesByReviewIds_returnsEmptyMap_whenNoLikes() {
        List<Long> reviewIds = List.of(review.getReviewId());

        Map<Long, Long> likeCounts = querydslRepository.countLikesByReviewIds(reviewIds);

        assertThat(likeCounts).isEmpty();
    }

    @Test
    void countLikesByReviewIds_returnsCorrectCounts() {
        ReviewLike like1 = ReviewLike.of(review, 1L);
        ReviewLike like2 = ReviewLike.of(review, 2L);

        em.persist(like1);
        em.persist(like2);

        em.flush();
        em.clear();

        List<Long> reviewIds = List.of(review.getReviewId());

        Map<Long, Long> likeCounts = querydslRepository.countLikesByReviewIds(reviewIds);

        assertThat(likeCounts)
                .hasSize(1)
                .containsEntry(review.getReviewId(), 2L);
    }
}