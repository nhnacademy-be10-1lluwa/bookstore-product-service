package com.nhnacademy.illuwa.d_review.review;

import com.nhnacademy.illuwa.common.client.member.MemberServiceClient;
import com.nhnacademy.illuwa.common.client.order.OrderServiceClient;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.entity.ReviewImage;
import com.nhnacademy.illuwa.d_review.review.exception.CannotWriteReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewImageRepository;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import com.nhnacademy.illuwa.d_review.review.service.Impl.ReviewServiceImpl;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import com.nhnacademy.illuwa.infra.storage.UploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceUnitTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private MemberServiceClient memberServiceClient;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MinioStorageService minioStorageService;

    private Book book;

    private final LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 5, 20, 41, 50);

    private final MockMultipartFile image = new MockMultipartFile(
            "images",
            "image1.png",
            "image/png",
            "fake-image-data".getBytes()
    );

    @BeforeEach
    void setUp() {
        String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";
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
    }

    @Test
    @DisplayName("리뷰 생성")
    void createReviewTest() {
        // given
        Long memberId = 99L;
        // 이미지 있는 경우
        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, List.of(image), null);
        MockMultipartFile image2 = new MockMultipartFile(
                " ",
                "",
                "",
                (byte[]) null
        );
        // 이미지 없는 경우
        ReviewRequest request2 = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, List.of(image2), null);
        ReviewRequest request3 = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, List.of(), null);

        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating(),
                fixedTime,
                book,
                memberId
        );

        ReviewImage reviewImage = ReviewImage.of(
                "https://test.com/review/Review/99/image1.png",
                review
        );


        // mock
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reviewRepository.save(any(Review.class))).thenReturn(review);
        Mockito.when(reviewImageRepository.save(any(ReviewImage.class))).thenReturn(reviewImage);
        Mockito.when(orderServiceClient.isConfirmedOrder(memberId, book.getId())).thenReturn(ResponseEntity.ok(true));
        Mockito.when(memberServiceClient.earnEventPoint(memberId, "PHOTO_REVIEW")).thenReturn(ResponseEntity.noContent().build());
        Mockito.when(minioStorageService.uploadReviewImage(Mockito.anyLong(), any(MultipartFile.class)))
                .thenReturn(new UploadResponse("image1.png",
                        "https://test.com/review/Review/99/image1.png",
                        "Review/99/UUID_image1.png"
                ));
        ReviewResponse response = reviewService.createReview(book.getId(), memberId, request); // 이미지 있는 경우
        ReviewResponse response2 = reviewService.createReview(book.getId(), memberId, request2); // 이미지 없는 경우
        ReviewResponse response3 = reviewService.createReview(book.getId(), memberId, request3);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewTitle()).isEqualTo("리뷰리뷰리뷰");
        assertThat(response.getReviewContent()).isEqualTo("포인트 냠냠");
        assertThat(response.getReviewRating()).isEqualTo(5);
        assertThat(response.getReviewDate()).isEqualTo(fixedTime);
        assertThat(response.getReviewImageUrls().size()).isEqualTo(1);
        assertThat(response2.getReviewImageUrls().isEmpty()).isTrue();
        assertThat(response3.getReviewImageUrls().isEmpty()).isTrue();
        assertThat(response.getBookId()).isEqualTo(999999L);
        assertThat(response.getMemberId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("구매를 확정하지 않은 사람이 리뷰작성 시도")
    void createReviewWhenUserDidNotConfirmedOrderTest() {
        // given
        Long memberId = 99L;
        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, List.of(image), null);

        // mock
        Mockito.when(orderServiceClient.isConfirmedOrder(memberId, book.getId())).thenReturn(ResponseEntity.ok(false));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(book.getId(), memberId, request))
                .isInstanceOf(CannotWriteReviewException.class)
                .hasMessage("구매가 확정되지 않아서 리뷰를 작성하실 수 없습니다!");
    }

    @Test
    @DisplayName("리뷰 중복 작성 시도")
    void createReviewWhenAlreadyCreatedTest() throws Exception {
        // given
        Long memberId = 99L;
        ReviewRequest request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 5, List.of(image), null);
        Review review = Review.of(
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getReviewRating(),
                fixedTime,
                book,
                memberId
        );

        // mock
        Mockito.when(reviewRepository.findByBook_IdAndMemberId(book.getId(), memberId)).thenReturn(Optional.of(review));
        Mockito.when(orderServiceClient.isConfirmedOrder(memberId, book.getId())).thenReturn(ResponseEntity.ok(true));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(book.getId(), memberId, request))
                .isInstanceOf(CannotWriteReviewException.class)
                .hasMessage("리뷰 중복 작성은 불가능합니다!");
    }

    @Test
    @DisplayName("리뷰 목록 가져오기")
    void getReviewPagesTest(){
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());
        List<LocalDateTime> date = new ArrayList<>();
        List<Review> savedReviews = new ArrayList<>();
        for(int i=0; i<5; i++) {
            date.add(fixedTime.plusDays(i));
            Review review = new Review(
                    (long) i +100,
                    "title" + (i + 1),
                    "content" + (i + 1),
                    i + 1,
                    date.get(i),
                    book,
                    (long) i + 1
            );
            savedReviews.add(review);
        }

        Page<Review> reviewPage = new PageImpl<>(savedReviews, pageable, 5);

        List<ReviewImage> allImages = savedReviews.stream()
                .map(review -> ReviewImage.of("https://test.com/review/rev_" + review.getReviewId() + ".png", review))
                .toList();

        // mock
        Mockito.when(reviewRepository.findReviewsByBook_Id(book.getId(), pageable)).thenReturn(reviewPage);
        Mockito.when(reviewImageRepository.findAllByReview_ReviewIdIn(savedReviews.stream().map(Review::getReviewId).toList())).thenReturn(allImages);

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
    @DisplayName("회원별 리뷰 페이지 조회 성공")
    void getMemberReviewPagesTest() {
        // given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "reviewDate"));
        Review review1 = new Review(10L, "제목A", "내용A", 5, fixedTime, book, memberId);
        Review review2 = new Review(11L, "제목B", "내용B", 4, fixedTime.plusDays(1), book, memberId);
        List<Review> reviews = List.of(review1, review2);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 2);

        ReviewImage image1 = ReviewImage.of("http://image.com/10/a.png", review1);
        ReviewImage image2 = ReviewImage.of("http://image.com/11/x.png", review2);

        // mock
        Mockito.when(reviewRepository.findReviewsByMemberId(memberId, pageable)).thenReturn(reviewPage);
        Mockito.when(reviewImageRepository.findAllByReview_ReviewIdIn(List.of(10L, 11L))).thenReturn(List.of(image1, image2));

        // when
        Page<ReviewResponse> result = reviewService.getMemberReviewPages(memberId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getReviewId()).isEqualTo(10L);
        assertThat(result.getContent().get(1).getReviewId()).isEqualTo(11L);
        assertThat(result.getContent().get(0).getReviewImageUrls()).containsExactly("http://image.com/10/a.png");
        assertThat(result.getContent().get(1).getReviewImageUrls()).containsExactly("http://image.com/11/x.png");
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    void getReviewDetailsTest() {
        // given
        Long bookId = 999L;
        Long reviewId = 777L;
        Long memberId = 1L;
        Review review = new Review(reviewId, "제목X", "내용Y", 3, fixedTime, book, memberId);
        List<ReviewImage> images = List.of(ReviewImage.of("http://img.com/777/img1.png", review));

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));
        Mockito.when(reviewImageRepository.findAllByReview_ReviewId(reviewId)).thenReturn(images);

        // when
        ReviewResponse response = reviewService.getReviewDetails(bookId, reviewId, memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewId()).isEqualTo(reviewId);
        assertThat(response.getReviewImageUrls()).isEqualTo(List.of("http://img.com/777/img1.png"));
    }

    @Test
    @DisplayName("리뷰 수정 - 성공")
    void updateReview_SuccessTest() throws Exception {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        ReviewRequest req = new ReviewRequest(
                "변경된 제목", "변경된 내용", 4,
                List.of(image),              // 새 이미지 있음
                List.of("http://old.com/10/old.png") // 기존 이미지 삭제 요청
        );

        Review review = new Review(reviewId, "원본 제목", "원본 내용", 5, fixedTime, book, memberId);
        ReviewImage oldImage = ReviewImage.of("http://old.com/10/old.png", review);
        ReviewImage newImage = ReviewImage.of("http://newimg.com/10/image1.png", review);

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));

        Mockito.when(minioStorageService.uploadReviewImage(eq(memberId), any()))
                .thenReturn(new UploadResponse("image1.png", "http://newimg.com/10/image1.png", "Review/10/UUID_img.png"));

        Mockito.when(reviewImageRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Mockito.when(reviewImageRepository.findAllByReview_ReviewId(reviewId))
                .thenReturn(List.of(oldImage))  // 삭제 전 기존 이미지
                .thenReturn(List.of(newImage)); // 삭제 후 새 이미지만 존재

        Mockito.doNothing().when(minioStorageService).deleteFile(anyString(), eq(memberId));

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, req);

        assertThat(response.getReviewTitle()).isEqualTo("변경된 제목");
        assertThat(response.getReviewContent()).isEqualTo("변경된 내용");
        assertThat(response.getReviewRating()).isEqualTo(4);
        assertThat(response.getReviewImageUrls()).containsOnly("http://newimg.com/10/image1.png");

        Mockito.verify(minioStorageService).uploadReviewImage(eq(memberId), any());
        Mockito.verify(minioStorageService).deleteFile("old.png", memberId);
        Mockito.verify(reviewImageRepository).delete(oldImage);
    }

    @Test
    @DisplayName("리뷰 수정 - 리뷰가 존재하지 않음")
    void updateReview_reviewNotFoundTest() {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        ReviewRequest req = new ReviewRequest("제목", "내용", 3, List.of(), List.of());

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(bookId, reviewId, memberId, req))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("리뷰를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("리뷰 수정 - 작성자 불일치")
    void updateReview_memberIdMismatchTest() {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        ReviewRequest req = new ReviewRequest("제목", "내용", 3, List.of(), List.of());
        Review review = new Review(reviewId, "제목", "내용", 5, fixedTime, book, 999L); // 다른 작성자

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(bookId, reviewId, memberId, req))
                .isInstanceOf(MemberIdDoesNotMatchWithReviewException.class)
                .hasMessageContaining("해당글의 작성자가 아닙니다");
    }

    @Test
    @DisplayName("리뷰 수정 - 새 이미지 업로드 없음, 기존 이미지 삭제 요청 없음")
    void updateReview_noNewImagesNoDelete() throws Exception {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        ReviewRequest req = new ReviewRequest("제목변경", "내용변경", 3, List.of(), null);

        Review review = new Review(reviewId, "원본", "내용", 5, fixedTime, book, memberId);

        // 기존 이미지가 없음
        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));
        Mockito.when(reviewImageRepository.findAllByReview_ReviewId(reviewId))
                .thenReturn(List.of());  // 빈 이미지 리스트

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, req);

        assertThat(response.getReviewTitle()).isEqualTo("제목변경");
        assertThat(response.getReviewContent()).isEqualTo("내용변경");
        assertThat(response.getReviewRating()).isEqualTo(3);
        assertThat(response.getReviewImageUrls()).isEmpty();

        Mockito.verify(minioStorageService, never()).uploadReviewImage(anyLong(), any());
        Mockito.verify(minioStorageService, never()).deleteFile(anyString(), anyLong());
    }

    @Test
    @DisplayName("리뷰 수정 - 삭제할 이미지 목록에 현재 이미지가 없는 경우")
    void updateReview_deleteImageSetDoesNotContained() throws Exception {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        String deleteImageUrl = "http://old.com/10/delete.png";

        ReviewRequest req = new ReviewRequest(
                "변경된 제목",
                "변경된 내용",
                4,
                List.of(image),
                List.of(deleteImageUrl)
        );

        Review review = new Review(reviewId, "원본 제목", "원본 내용", 5, fixedTime, book, memberId);

        ReviewImage oldImageToDelete = ReviewImage.of(deleteImageUrl, review);
        ReviewImage oldImageToKeep = ReviewImage.of("http://old.com/10/keep.png", review);

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));

        Mockito.when(minioStorageService.uploadReviewImage(eq(memberId), any()))
                .thenReturn(new UploadResponse("image1.png",
                        "http://newimg.com/10/image1.png",
                        "Review/10/UUID_img.png"));

        Mockito.when(reviewImageRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(reviewImageRepository.findAllByReview_ReviewId(reviewId))
                .thenReturn(List.of(oldImageToDelete, oldImageToKeep))
                .thenReturn(List.of(oldImageToKeep));

        Mockito.doNothing().when(minioStorageService).deleteFile("delete.png", memberId);
        Mockito.doNothing().when(reviewImageRepository).delete(oldImageToDelete);

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, req);

        assertThat(response.getReviewTitle()).isEqualTo("변경된 제목");
        assertThat(response.getReviewContent()).isEqualTo("변경된 내용");
        assertThat(response.getReviewImageUrls()).containsExactly("http://old.com/10/keep.png");

        Mockito.verify(minioStorageService).deleteFile("delete.png", memberId);
        Mockito.verify(reviewImageRepository).delete(oldImageToDelete);
    }

    @Test
    @DisplayName("리뷰 수정 - 새 이미지 리스트 첫 파일명 빈 문자열인 경우 업로드 생략")
    void updateReview_newImagesWithBlankFilename() throws Exception {
        Long memberId = 1L;
        Long reviewId = 10L;
        Long bookId = 100L;

        MockMultipartFile blankNameImage = new MockMultipartFile(
                "images", "", "image/png", "data".getBytes()
        );

        ReviewRequest req = new ReviewRequest("제목", "내용", 5, List.of(blankNameImage), null);

        Review review = new Review(reviewId, "원본제목", "원본내용", 5, fixedTime, book, memberId);

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId))
                .thenReturn(Optional.of(review));
        Mockito.when(reviewImageRepository.findAllByReview_ReviewId(reviewId))
                .thenReturn(List.of());

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, req);

        assertThat(response.getReviewImageUrls()).isEmpty();

        Mockito.verify(minioStorageService, never()).uploadReviewImage(anyLong(), any());
    }

    @Test
    @DisplayName("리뷰 수정 - 작성자 아님")
    void updateReview_NotMatchMemberId_ThrowsException() {
        Long memberId = 12L;
        Long reviewId = 100L;
        Long bookId = 200L;
        ReviewRequest req = new ReviewRequest("수정", "내용", 3, List.of(), null);
        Review review = new Review(reviewId, "제목1", "내용2", 5, fixedTime, book, 777L);

        Mockito.when(reviewRepository.findByBook_IdAndReviewId(bookId, reviewId)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(bookId, reviewId, memberId, req))
                .isInstanceOf(MemberIdDoesNotMatchWithReviewException.class)
                .hasMessageContaining("해당글의 작성자가 아닙니다.");
    }

    @Test
    @DisplayName("리뷰 존재하는 Book-Review 매핑")
    void getExistingReviewIdMapTest() {
        // given
        Long memberId = 33L;
        Review reviewA = new Review(300L, "t", "c", 5, fixedTime, book, memberId);
        Review reviewB = new Review(301L, "t2", "c2", 4, fixedTime, book, memberId);

        List<Long> bookIds = List.of(100L, 200L, 300L);
        Mockito.when(reviewRepository.findByBook_IdAndMemberId(eq(100L), eq(memberId))).thenReturn(Optional.of(reviewA));
        Mockito.when(reviewRepository.findByBook_IdAndMemberId(eq(200L), eq(memberId))).thenReturn(Optional.empty());
        Mockito.when(reviewRepository.findByBook_IdAndMemberId(eq(300L), eq(memberId))).thenReturn(Optional.of(reviewB));

        // when
        Map<Long, Long> result = reviewService.getExistingReviewIdMap(bookIds, memberId);

        // then
        assertThat(result).containsEntry(100L, 300L).containsEntry(300L, 301L);
        assertThat(result.keySet()).containsExactlyInAnyOrderElementsOf(List.of(100L, 300L));
    }

    @Test
    @DisplayName("리뷰 ID로 도서명 map 조회")
    void getBookTitleMapFromReviewIdsTest() {
        Map<Long, String> mockMap = Map.of(111L, "책A", 222L, "책B", 333L, "책C");
        Collection<Long> reviewIds = List.of(111L, 222L, 333L);

        Mockito.when(reviewRepository.findBookTitleMapByReviewIds(reviewIds)).thenReturn(mockMap);

        // when
        Map<Long, String> result = reviewService.getBookTitleMapFromReviewIds(reviewIds);

        // then
        assertThat(result).isEqualTo(mockMap);
    }
}