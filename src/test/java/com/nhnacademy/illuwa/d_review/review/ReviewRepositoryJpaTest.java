package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
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
                "11111111111111111",
                100,
                100,
                false,
                "book.jpg",
                "카테고리없음"
        );
        testEntityManager.persist(book);
    }

    @Test
    @DisplayName("리뷰 목록 가져오기 테스트")
    void findReviewsByBookIdTest(){
        //given
        List<LocalDateTime> now = new ArrayList<>();
        List<Review> savedReviews = new ArrayList<>();
        for(int i=0; i<5; i++){
            now.add(LocalDateTime.now());
            Review review = Review.of(
                    "title"+(i+1),
                    "content"+(i+1),
                    "img"+(i+1)+".jpg",
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
        List<Review> reviews = reviewRepository.findReviewsByBook_Id(book.getId());

        assertThat(reviews.size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            Review review = reviews.get(i);
            assertThat(review.getReviewId()).isEqualTo(savedReviews.get(i).getReviewId());
            assertThat(review.getReviewTitle()).isEqualTo("title"+(i+1));
            assertThat(review.getReviewContent()).isEqualTo("content"+(i+1));
            assertThat(review.getReviewImageUrl()).isEqualTo("img"+(i+1)+".jpg");
            assertThat(review.getReviewRating()).isEqualTo(i+1);
            assertThat(review.getReviewDate()).isCloseTo(now.get(i), within(1, ChronoUnit.SECONDS));
            assertThat(review.getBook().getId()).isEqualTo(book.getId());
            assertThat(review.getReviewId()).isEqualTo((long)i+1);
        }
    }

    @Test
    @DisplayName("리뷰 상세보기 테스트")
    void findByBookIdAndReviewIdTest() {
        Review review = Review.of(
                "책이 재미 없어요",
                "종이가 아깝습니다",
                "book.jpg",
                1,
                LocalDateTime.now(),
                book,
                99L
        );
        testEntityManager.persist(review);

        testEntityManager.flush();
        testEntityManager.clear();

        // when
        Optional<Review> result = reviewRepository.findByBook_IdAndReviewId(book.getId(), review.getReviewId());

        // then
        assertThat(result.isPresent()).isTrue();
        if(result.isPresent()){
            Review found = result.get();

            assertThat(found.getReviewId()).isEqualTo(review.getReviewId());
            assertThat(found.getReviewTitle()).isEqualTo("책이 재미 없어요");
            assertThat(found.getReviewContent()).isEqualTo("종이가 아깝습니다");
            assertThat(found.getReviewImageUrl()).isEqualTo("book.jpg");
            assertThat(found.getReviewRating()).isEqualTo(1);
            assertThat(found.getBook().getId()).isEqualTo(book.getId());
            assertThat(found.getMemberId()).isEqualTo(99L);
        }
    }
}