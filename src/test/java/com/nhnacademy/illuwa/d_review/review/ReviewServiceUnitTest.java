package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceUnitTest {
    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

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
                null,
                new BookExtraInfo(Status.DELETED,true,1)
        );
    }

    @Test
    @DisplayName("이미지 있는 리뷰 생성")
    void createReviewTest() throws Exception {
        // given
        Long memberId = 99L;

        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, null);

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating(),
                now,
                book,
                memberId
        );

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(book.getId(), request, memberId, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("존재하지 않는 책의 리뷰 작성")
    void createReviewWithInvalidBookTest() {
        // given
        Long memberId = 99L;
        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, null);

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(book.getId(), request, memberId, null))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("도서를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 목록 가져오기")
    void getReviewPagesTest(){
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());
        List<LocalDateTime> now = new ArrayList<>();
        List<Review> savedReviews = new ArrayList<>();
        for(int i=0; i<5; i++) {
            now.add(LocalDateTime.now());
            Review review = Review.of(
                    "title" + (i + 1),
                    "content" + (i + 1),
                    i + 1,
                    now.get(i),
                    book,
                    (long) i + 1
            );
            savedReviews.add(review);
        }

        Page<Review> reviewPage = new PageImpl<>(savedReviews, pageable, 5);

        // mock
        Mockito.when(reviewRepository.findReviewsByBook_Id(book.getId(), pageable)).thenReturn(reviewPage);

        // when
        Page<ReviewResponse> response = reviewService.getReviewPages(book.getId(), pageable);

        // then
        assertThat(response.getContent().size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            Review review = savedReviews.get(i);
            ReviewResponse reviewResponse = response.getContent().get(i);

            assertThat(reviewResponse.getReviewId()).isEqualTo(review.getReviewId());
            assertThat(reviewResponse.getReviewTitle()).isEqualTo(review.getReviewTitle());
            assertThat(reviewResponse.getReviewContent()).isEqualTo(review.getReviewContent());
            assertThat(reviewResponse.getReviewRating()).isEqualTo(review.getReviewRating());
            assertThat(reviewResponse.getReviewDate()).isEqualTo(review.getReviewDate());
            assertThat(reviewResponse.getBookId()).isEqualTo(review.getBook().getId());
            assertThat(reviewResponse.getMemberId()).isEqualTo(review.getMemberId());
        }
    }

    @Test
    @DisplayName("리뷰 업데이트")
    void updateReview() throws Exception {
//        // given
//        Long memberId = 999L;
//
//        Review review = Review.of(
//                "오래된 리뷰",
//                "오래된 내용",
//                5,
//                LocalDateTime.now(),
//                book,
//                memberId
//        );
//
//        ReviewRequest request = new ReviewRequest("갱신된 리뷰", "갱신된 내용", 3, null);
//
//        // mock
//        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), review.getReviewId())).thenReturn(Optional.of(review));
//
//        // when
//        ReviewResponse response = reviewService.updateReview(book.getId(), review.getReviewId(), request, memberId, null);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getReviewTitle()).isEqualTo("갱신된 리뷰");
//        assertThat(response.getReviewContent()).isEqualTo("갱신된 내용");
//        assertThat(response.getReviewRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 수정")
    void updateReviewNotFoundTest(){
        // given
        Long memberId = 7777L;
        Long reviewId = 99999L;
        ReviewRequest updateRequest = new ReviewRequest("제목", "내용", 5, null);

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), reviewId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> reviewService.updateReview(book.getId(), reviewId, updateRequest, memberId, null))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("리뷰를 찾을 수 없습니다.");
    }
}