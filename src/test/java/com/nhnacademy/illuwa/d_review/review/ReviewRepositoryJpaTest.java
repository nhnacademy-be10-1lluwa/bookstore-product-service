package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class ReviewRepositoryJpaTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Book book;
    @BeforeEach
    void setUp() {
        book = new Book(
                null,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.now().minusYears(1L),
                "1111111111111",
                100,
                100,
                null,
                new BookExtraInfo(Status.DELETED,true,1)
        );
        testEntityManager.persist(book);
    }

    @Test
    @DisplayName("리뷰 목록 가져오기 테스트")
    void findReviewsByBookIdTest(){
        //given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").ascending());
        List<LocalDateTime> now = new ArrayList<>();
        List<Review> savedReviews = new ArrayList<>();
        for(int i=0; i<5; i++){
            now.add(LocalDateTime.now());
            Review review = Review.of(
                    "title"+(i+1),
                    "content"+(i+1),
                    i+1,
                    now.get(i),
                    book,
                    (long) i+1
            );
            testEntityManager.persist(review);
            savedReviews.add(review);
        }
        testEntityManager.flush();
        testEntityManager.clear();

        // when
        Page<Review> reviewPages = reviewRepository.findReviewsByBook_Id(book.getId(), pageable);

        assertThat(reviewPages.getContent().size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            Review review = reviewPages.getContent().get(i);
            assertThat(review.getReviewId()).isEqualTo(savedReviews.get(i).getReviewId());
            assertThat(review.getReviewTitle()).isEqualTo("title"+(i+1));
            assertThat(review.getReviewContent()).isEqualTo("content"+(i+1));
            assertThat(review.getReviewRating()).isEqualTo(i+1);
            assertThat(review.getReviewDate()).isCloseTo(now.get(i), within(1, ChronoUnit.SECONDS));
            assertThat(review.getBook().getId()).isEqualTo(book.getId());
        }
    }
}