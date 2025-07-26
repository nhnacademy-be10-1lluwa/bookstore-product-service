package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceUnitTest {

    @InjectMocks
    private ReviewLikeServiceImpl reviewLikeService;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private Review review;
    private Long reviewId = 1L;
    private Long memberId = 100L;

    @BeforeEach
    void setUp() {
        Book book = new Book(
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
                List.of(),
                null,
                null,
                null
        );

        review = new Review(
                reviewId,
                "리뷰 제목",
                "리뷰 내용",
                5,
                LocalDateTime.now(),
                book,
                memberId
        );
    }

    @Test
    void toggleLike_whenNotLiked_shouldSaveAndReturnTrue() {
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(false);
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(10L);
        Mockito.when(reviewLikeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewLikeResponse response = reviewLikeService.toggleLike(reviewId, memberId);

        assertThat(response.isLikedByMe()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(10L);

        Mockito.verify(reviewLikeRepository).save(any());
        Mockito.verify(reviewLikeRepository, Mockito.never()).deleteByReview_ReviewIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    void toggleLike_whenAlreadyLiked_shouldDeleteAndReturnFalse() {
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(true);
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(5L);

        ReviewLikeResponse response = reviewLikeService.toggleLike(reviewId, memberId);

        assertThat(response.isLikedByMe()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(5L);

        Mockito.verify(reviewLikeRepository).deleteByReview_ReviewIdAndMemberId(reviewId, memberId);
        Mockito.verify(reviewLikeRepository, Mockito.never()).save(any());
    }

    @Test
    void toggleLike_whenReviewNotFound_shouldThrow() {
        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewLikeService.toggleLike(reviewId, memberId))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("리뷰를 찾을 수 없습니다");
    }

    @Test
    void getLikeCountsFromReviews_shouldReturnMap() {
        List<Long> reviewIds = List.of(1L, 2L, 3L);
        Map<Long, Long> expected = Map.of(1L, 10L, 2L, 5L);
        Mockito.when(reviewLikeRepository.countLikesByReviewIds(reviewIds)).thenReturn(expected);

        Map<Long, Long> result = reviewLikeService.getLikeCountsFromReviews(reviewIds);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getMyLikedReviews_shouldReturnList() {
        List<Long> reviewIds = List.of(1L, 2L, 3L);
        List<Long> likedIds = List.of(1L, 3L);
        Mockito.when(reviewLikeRepository.findMyLikedReviewIds(reviewIds, memberId)).thenReturn(likedIds);

        List<Long> result = reviewLikeService.getMyLikedReviews(reviewIds, memberId);

        assertThat(result).containsExactlyInAnyOrderElementsOf(likedIds);
    }

    @Test
    void isLikedByMe_whenLiked_thenTrue() {
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(true);

        boolean result = reviewLikeService.isLikedByMe(reviewId, memberId);

        assertThat(result).isTrue();
    }

    @Test
    void isLikedByMe_whenNotLiked_thenFalse() {
        Mockito.when(reviewLikeRepository.existsByReview_ReviewIdAndMemberId(reviewId, memberId)).thenReturn(false);

        boolean result = reviewLikeService.isLikedByMe(reviewId, memberId);

        assertThat(result).isFalse();
    }

    @Test
    void getLikeCount_shouldReturnCount() {
        Mockito.when(reviewLikeRepository.countByReview_ReviewId(reviewId)).thenReturn(7L);

        long count = reviewLikeService.getLikeCount(reviewId);

        assertThat(count).isEqualTo(7L);
    }
}