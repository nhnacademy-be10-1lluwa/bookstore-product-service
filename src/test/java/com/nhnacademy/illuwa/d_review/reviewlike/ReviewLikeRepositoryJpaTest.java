package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.ReviewLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class ReviewLikeRepositoryJpaTest {
    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Review review;

    @BeforeEach
    public void setUp() {
        Book book = new Book(
                null,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(1970,1,1),
                "11111111111111111",
                100,
                100,
                false,
                "book.jpg",
                "카테고리없음"
        );
        testEntityManager.persist(book);

        review = Review.of(
                "리뷰 제목",
                "리뷰 내용",
                "/img/review.jpg",
                5,
                LocalDateTime.of(2099, 8, 22, 4, 17, 32),
                book,
                9999L
        );
        testEntityManager.persist(review);

        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    @DisplayName("좋아요 수 표시 테스트")
    void countByReview_ReviewIdTest() {
        // given (BeforeEach)
        for(int i=0; i<5; i++){
            reviewLikeRepository.save(ReviewLike.of(review, 10L+i));
        }

        // when
        long count = reviewLikeRepository.countByReview_ReviewId(review.getReviewId());

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("좋아요 추가 테스트")
    void addLikeTest(){
        // given
        Long memberId = 7777L;

        // when
        ReviewLike save = reviewLikeRepository.save(ReviewLike.of(review, memberId));

        // then
        assertThat(reviewLikeRepository.countByReview_ReviewId(review.getReviewId())).isEqualTo(1);
        assertThat(save.getLikeId()).isNotNull();
        assertThat(save.getReview().getReviewId()).isEqualTo(review.getReviewId());
        assertThat(save.getMemberId()).isEqualTo(7777L);
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void deleteByReview_ReviewIdAndMemberIdTest(){
        //given
        Long reviewId = review.getReviewId();
        Long targetMemberId = 100L;

        for(int i=0; i<5; i++){
            reviewLikeRepository.save(ReviewLike.of(review, 100L+i));
        }

        // when
        reviewLikeRepository.deleteByReview_ReviewIdAndMemberId(reviewId, targetMemberId);

        // then
        assertThat(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, targetMemberId)).isFalse();
        assertThat(reviewLikeRepository.countByReview_ReviewId(reviewId)).isEqualTo(4);
    }
}