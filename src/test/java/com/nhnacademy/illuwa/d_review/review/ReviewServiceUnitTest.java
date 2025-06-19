package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponseList;
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
                false,
                "book.jpg"
        );
    }

    @Test
    @DisplayName("이미지 있는 리뷰 생성")
    void createReviewTest() {
        // given
        Long memberId = 99L;

        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", "blank.png", 5);

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewImageUrl(),
                request.getReviewRating(),
                now,
                book,
                memberId
        );

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(book.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewImageUrl()).isEqualTo("blank.png");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("이미지 없는 리뷰 생성 (Null)")
    void createReviewImageNullTest() {
        // given
        Long memberId = 99L;

        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", null, 5);

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewImageUrl(),
                request.getReviewRating(),
                now,
                book,
                memberId
        );

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(book.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewImageUrl()).isNull();
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("이미지 없는 리뷰 생성 (Blank)")
    void createReviewImageBlankTest() {
        // given
        Long memberId = 99L;

        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", " ", 5);

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewImageUrl(),
                request.getReviewRating(),
                now,
                book,
                memberId
        );

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(book.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewImageUrl()).isBlank();
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("존재하지 않는 책의 리뷰 작성")
    void createReviewWithInvalidBookTest() {
        // given
        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", null, 5);

        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(book.getId(), request))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("도서를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 목록 가져오기")
    void getReviewListTest(){
        // given
        List<LocalDateTime> now = new ArrayList<>();
        List<Review> savedReviews = new ArrayList<>();
        for(int i=0; i<5; i++) {
            now.add(LocalDateTime.now());
            Review review = Review.of(
                    "title" + (i + 1),
                    "content" + (i + 1),
                    "img" + (i + 1) + ".jpg",
                    i + 1,
                    now.get(i),
                    book,
                    (long) i + 1
            );
            savedReviews.add(review);
        }

        // mock
        Mockito.when(reviewRepository.findReviewsByBook_Id(book.getId())).thenReturn(savedReviews);

        // when
        ReviewResponseList response = reviewService.getReviewList(book.getId());

        // then
        assertThat(response.getReviews().size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            Review review = savedReviews.get(i);
            ReviewResponse reviewResponse = response.getReviews().get(i);

            assertThat(reviewResponse.getReviewId()).isEqualTo(review.getReviewId());
            assertThat(reviewResponse.getReviewTitle()).isEqualTo(review.getReviewTitle());
            assertThat(reviewResponse.getReviewContent()).isEqualTo(review.getReviewContent());
            assertThat(reviewResponse.getReviewImageUrl()).isEqualTo(review.getReviewImageUrl());
            assertThat(reviewResponse.getReviewRating()).isEqualTo(review.getReviewRating());
            assertThat(reviewResponse.getReviewDate()).isEqualTo(review.getReviewDate());
            assertThat(reviewResponse.getBookId()).isEqualTo(review.getBook().getId());
            assertThat(reviewResponse.getMemberId()).isEqualTo(review.getMemberId());
        }
    }

    @Test
    @DisplayName("리뷰 상세 페이지 가져오기")
    void getReviewDetailTest(){
        // given
        Long memberId = 999L;
        
        LocalDateTime now = LocalDateTime.now();
        
        Review review = Review.of(
                "리뷰리뷰리뷰",
                "포인트 냠냠",
                "strange_book.jpg",
                5,
                now,
                book,
                memberId
        );

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), review.getReviewId())).thenReturn(Optional.of(review));
        
        // when
        ReviewResponse response = reviewService.getReviewDetail(book.getId(), review.getReviewId());
        
        // then
        assertThat(response).isNotNull();
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewImageUrl()).isEqualTo("strange_book.jpg");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(now);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getMemberId()).isEqualTo(999L);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회")
    void getReviewDetailNotFoundTest(){
        // given
        Long reviewId = 99999L;

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), reviewId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> reviewService.getReviewDetail(book.getId(), reviewId))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("StatusCode: " + ReviewNotFoundException.getStatusC0de() + " 리뷰를 찾을 수 없습니다. Review ID: " + reviewId);
    }


    @Test
    @DisplayName("리뷰 업데이트")
    void updateReview() {
        // given
        Long memberId = 999L;

        Review review = Review.of(
                "오래된 리뷰",
                "오래된 내용",
                "old.jpg",
                5,
                LocalDateTime.now(),
                book,
                memberId
        );

        ReviewRequest request = new ReviewRequest("갱신된 리뷰", "갱신된 내용", "new.jpg", 3);

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), review.getReviewId())).thenReturn(Optional.of(review));

        // when
        ReviewResponse response = reviewService.updateReview(book.getId(), review.getReviewId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("갱신된 리뷰");
        assertThat(response.getReviewContent()).isEqualTo("갱신된 내용");
        assertThat(response.getReviewImageUrl()).isEqualTo("new.jpg");
        assertThat(response.getReviewRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 수정")
    void updateReviewNotFoundTest(){
        // given
        Long reviewId = 99999L;
        ReviewRequest updateRequest = new ReviewRequest("제목", "내용", null, 5);

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(book.getId(), reviewId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> reviewService.updateReview(book.getId(), reviewId, updateRequest))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("StatusCode: " + ReviewNotFoundException.getStatusC0de() + " 리뷰를 찾을 수 없습니다. Review ID: " + reviewId);
    }
}