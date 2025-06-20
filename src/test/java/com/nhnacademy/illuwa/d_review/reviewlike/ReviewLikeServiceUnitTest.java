package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import com.nhnacademy.illuwa.d_review.reviewlike.repository.ReviewLikeRepository;
import com.nhnacademy.illuwa.d_review.reviewlike.service.Impl.ReviewLikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class ReviewLikeServiceUnitTest {
    @InjectMocks
    private ReviewLikeServiceImpl reviewLikeService;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private Book book;

    private Review review;

    @BeforeEach
    void setUp() {
        book = new Book(
                10L,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(1970, 1, 1),
                "11111111111111111",
                100,
                100,
                false,
                "book.jpg"
        );

        review = new Review(
                1L,
                "리뷰 제목",
                "리뷰 내용",
                "/img/review.jpg",
                5,
                LocalDateTime.of(2099, 8, 22, 4, 17, 32),
                book,
                9999L
        );
    }

    @Test
    @DisplayName("좋아요 추가 테스트")
    void addLikeTest() {
        // given
        Long bookId = book.getId();
        Long reviewId = review.getReviewId();
        Long memberId = 7777L;
        ReviewLike reviewLike = ReviewLike.of(review, memberId);

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId)).thenReturn(Optional.of(review));
        Mockito.when(reviewLikeRepository.save(any(ReviewLike.class))).thenReturn(ReviewLike.of(review, memberId));
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(false, true);
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(1L);

        // when
        ReviewLikeResponse response = reviewLikeService.addLike(bookId, reviewId, memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isLikedByMe()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 취소 테스트")
    void cancelLikeTest() {
        // given
        Long reviewId = review.getReviewId();
        Long memberId = 7777L;

        // mock
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(true, false);
        doNothing().when(reviewLikeRepository).deleteByReview_ReviewIdAndMemberId(reviewId, memberId);
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(0L);

        // when
        ReviewLikeResponse response = reviewLikeService.cancelLike(reviewId, memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isLikedByMe()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 상태 표시")
    void getLikeInfoTest(){
        // given
        Long bookId = book.getId();
        Long reviewId = review.getReviewId();
        Long memberId = 7777L;

        // mock
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(true);
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(1L);

        // when
        ReviewLikeResponse response = reviewLikeService.getLikeInfo(reviewId, memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isLikedByMe()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(1);
    }
}
