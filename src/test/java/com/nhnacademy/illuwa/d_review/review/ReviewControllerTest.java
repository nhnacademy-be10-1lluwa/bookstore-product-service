package com.nhnacademy.illuwa.d_review.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_review.review.controller.ReviewController;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long bookId = 10L;
    private final Long reviewId = 1L;
    private final Long memberId = 99L;
    private final LocalDateTime fixedDate = LocalDateTime.of(2024, 2, 19, 21, 32, 11);

    private ReviewResponse response;

    private final MockMultipartFile image = new MockMultipartFile(
            "images", // ReviewRequest 필드명
            "image1.png",
            "image/png",
            "fake-image-data".getBytes()
    );

    @BeforeEach
    void setUp() {
        response = new ReviewResponse(reviewId, "리뷰리뷰리뷰", "포인트 냠냠", 4, fixedDate, bookId, memberId, List.of("http://testlocation/img/image1.png"));
    }

    @Test
    @DisplayName("리뷰 작성")
    public void reviewCreateTest() throws Exception {
        // given

        given(reviewService.createReview(eq(bookId), eq(memberId), any(ReviewRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/reviews")
                        .file(image)
                        .param("book-id", String.valueOf(bookId))
                        .param("reviewTitle", "리뷰리뷰리뷰")
                        .param("reviewContent", "포인트 냠냠")
                        .param("reviewRating", "4")
                        .header("X-USER-ID", memberId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.reviewRating").value(4))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));
        verify(reviewService).createReview(eq(bookId), eq(memberId), any(ReviewRequest.class));
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    public void getReviewPagesTest() throws Exception {
        // 리뷰 목록 설정
        Long reviewId2 = 2L;
        Long memberId2 = 22L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());

        ReviewResponse response2 = new ReviewResponse(reviewId2, "대충 리뷰 제목", "대충 내용", 3, fixedDate, bookId, memberId2, null);
        Page<ReviewResponse> reviewResponsePages = new PageImpl<>(Arrays.asList(response, response2));

        // given
        given(reviewService.getReviewPages(eq(bookId), eq(pageable))).willReturn(reviewResponsePages);

        // when & then
        mockMvc.perform(get("/api/public/reviews").param("book-id", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reviewId").value(reviewId))
                .andExpect(jsonPath("$.content[0].reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.content[0].reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.content[0].reviewRating").value(4))
                .andExpect(jsonPath("$.content[0].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.content[0].bookId").value(bookId))
                .andExpect(jsonPath("$.content[0].memberId").value(memberId))
                .andExpect(jsonPath("$.content[1].reviewId").value(reviewId2))
                .andExpect(jsonPath("$.content[1].reviewTitle").value("대충 리뷰 제목"))
                .andExpect(jsonPath("$.content[1].reviewContent").value("대충 내용"))
                .andExpect(jsonPath("$.content[1].reviewRating").value(3))
                .andExpect(jsonPath("$.content[1].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.content[1].bookId").value(bookId))
                .andExpect(jsonPath("$.content[1].memberId").value(memberId2));

        verify(reviewService).getReviewPages(eq(bookId), eq(pageable));
    }

    @Test
    @DisplayName("리뷰 수정 테스트")
    public void reviewUpdateTest() throws Exception {
        LocalDateTime fixedDate2 = LocalDateTime.of(2024, 3, 16, 12, 28, 43);
        ReviewResponse response2 = new ReviewResponse(reviewId, "대충 리뷰 수정함", "대충 내용 수정함", 5, fixedDate2, bookId, memberId, List.of("http://testlocation/img/image1.png"));

        // given
        given(reviewService.updateReview(eq(bookId), eq(reviewId), eq(memberId), any(ReviewRequest.class))).willReturn(response2);

        // when & then
        mockMvc.perform(multipart("/api/reviews/{review-id}", reviewId)
                        .param("book-id", String.valueOf(bookId))
                        .param("reviewTitle", "대충 리뷰 수정함")
                        .param("reviewContent", "대충 내용 수정함")
                        .param("reviewRating", "5")
                        .param("reviewDate", fixedDate2.toString())
                        .header("X-USER-ID", memberId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("대충 리뷰 수정함"))
                .andExpect(jsonPath("$.reviewContent").value("대충 내용 수정함"))
                .andExpect(jsonPath("$.reviewRating").value(5))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate2.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));

        verify(reviewService).updateReview(eq(bookId), eq(reviewId), eq(memberId), any(ReviewRequest.class));
    }

    @Test
    @DisplayName("특정 유저가 작성한 리뷰 목록 조회")
    public void getMemberReviewPagesTest() throws Exception {
        // 리뷰 목록 설정
        Long reviewId2 = 2L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());

        ReviewResponse response2 = new ReviewResponse(reviewId2, "대충 리뷰 제목", "대충 내용", 3, fixedDate, bookId, memberId, null);
        Page<ReviewResponse> reviewResponsePages = new PageImpl<>(Arrays.asList(response, response2));

        // given
        given(reviewService.getMemberReviewPages(eq(memberId), eq(pageable))).willReturn(reviewResponsePages);

        // when & then
        mockMvc.perform(get("/api/reviews")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-USER-ID", memberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reviewId").value(reviewId))
                .andExpect(jsonPath("$.content[0].reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.content[0].reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.content[0].reviewRating").value(4))
                .andExpect(jsonPath("$.content[0].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.content[0].bookId").value(bookId))
                .andExpect(jsonPath("$.content[0].memberId").value(memberId))
                .andExpect(jsonPath("$.content[1].reviewId").value(reviewId2))
                .andExpect(jsonPath("$.content[1].reviewTitle").value("대충 리뷰 제목"))
                .andExpect(jsonPath("$.content[1].reviewContent").value("대충 내용"))
                .andExpect(jsonPath("$.content[1].reviewRating").value(3))
                .andExpect(jsonPath("$.content[1].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.content[1].bookId").value(bookId))
                .andExpect(jsonPath("$.content[1].memberId").value(memberId));

        verify(reviewService).getMemberReviewPages(eq(memberId), eq(pageable));
    }

    @Test
    @DisplayName("리뷰 상세 가져오기")
    public void getReviewDetailsTest() throws Exception {
        // given
        given(reviewService.getReviewDetails(eq(bookId), eq(reviewId), eq(memberId))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/reviews/{review-id}", reviewId)
                        .param("book-id", String.valueOf(bookId))
                        .header("X-USER-ID", memberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.reviewRating").value(4))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));

        verify(reviewService).getReviewDetails(eq(bookId), eq(reviewId), eq(memberId));
    }

    @Test
    @DisplayName("자신이 쓴 리뷰 맵으로 가져오기")
    public void getExistingReviewIdMapTest() throws Exception {
        // given
        List<Long> bookIds = List.of(10L, 20L, 30L, 40L, 50L);
        Map<Long, Long> reviewIdMap = Map.of(
                10L, 15L,
                20L, 30L,
                30L, 45L,
                40L, 60L,
                50L, 75L
        );
        given(reviewService.getExistingReviewIdMap(eq(bookIds), eq(memberId))).willReturn(reviewIdMap);

        // when & then
        mockMvc.perform(post("/api/reviews/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", memberId)
                        .content(objectMapper.writeValueAsString(bookIds))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['10']").value(reviewIdMap.get(10L)))
                .andExpect(jsonPath("$['20']").value(reviewIdMap.get(20L)))
                .andExpect(jsonPath("$['30']").value(reviewIdMap.get(30L)))
                .andExpect(jsonPath("$['40']").value(reviewIdMap.get(40L)))
                .andExpect(jsonPath("$['50']").value(reviewIdMap.get(50L)));

        verify(reviewService).getExistingReviewIdMap(eq(bookIds), eq(memberId));
    }

    @Test
    @DisplayName("자신이 쓴 리뷰들의 책제목을 맵으로 가져오기")
    public void getBookTitleMapFromReviewIds() throws Exception{
        // given
        Collection<Long> reviewIds = List.of(10L, 20L, 30L, 40L, 50L);
        Map<Long, String> reviewIdMap = Map.of(
                10L, "테스트 책 1",
                20L, "테스트 책 2",
                30L, "테스트 책 3",
                40L, "테스트 책 4",
                50L, "테스트 책 5"
        );
        given(reviewService.getBookTitleMapFromReviewIds(eq(reviewIds))).willReturn(reviewIdMap);

        // when & then
        mockMvc.perform(get("/api/reviews/book-title")
                        .param("review-ids", "10", "20", "30", "40", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['10']").value("테스트 책 1"))
                .andExpect(jsonPath("$['20']").value("테스트 책 2"))
                .andExpect(jsonPath("$['30']").value("테스트 책 3"))
                .andExpect(jsonPath("$['40']").value("테스트 책 4"))
                .andExpect(jsonPath("$['50']").value("테스트 책 5"));

        verify(reviewService).getBookTitleMapFromReviewIds(eq(reviewIds));
    }
}
