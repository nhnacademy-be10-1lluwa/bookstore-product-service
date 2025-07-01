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

    private final Long bookId = 10L;
    private final Long reviewId = 1L;
    private final Long memberId = 99L;
    private final LocalDateTime fixedDate = LocalDateTime.of(2024, 2, 19, 21, 32, 11);

    private ReviewRequest request;
    private ReviewResponse response;

    @BeforeEach
    void setUp() {
        request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", 4, null);
        response = new ReviewResponse(reviewId, "리뷰리뷰리뷰", "포인트 냠냠", 4, fixedDate, bookId, memberId);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 작성")
    public void reviewCreateTest() throws Exception {
        // given
        MockMultipartFile reviewPart = new MockMultipartFile(
                "review", // @RequestPart name
                "", // 파일 null
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );
        given(reviewService.createReview(eq(bookId), any(ReviewRequest.class), eq(memberId), isNull())).willReturn(response);

        // when & then
        mockMvc.perform(multipart("/books/{bookId}/reviews", bookId)
                        .file(reviewPart)
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
        verify(reviewService).createReview(eq(bookId), any(ReviewRequest.class), eq(memberId), isNull());
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    public void getReviewListTest() throws Exception {
        // 리뷰 목록 설정
        Long reviewId2 = 2L;
        Long memberId2 = 22L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());

        ReviewResponse response2 = new ReviewResponse(reviewId2, "대충 리뷰 제목", "대충 내용", 3, fixedDate, bookId, memberId2);
        Page<ReviewResponse> reviewResponsePages = new PageImpl<>(Arrays.asList(response, response2));

        // given
        given(reviewService.getReviewPages(eq(bookId), eq(pageable))).willReturn(reviewResponsePages);

        // when & then
        mockMvc.perform(get("/books/{bookId}/reviews", bookId))
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
//        LocalDateTime fixedDate2 = LocalDateTime.of(2024, 3, 16, 12, 28, 43);
//        ReviewRequest request2 = new ReviewRequest("대충 리뷰 수정함", "대충 내용 수정함", 5, null);
//        ReviewResponse response2 = new ReviewResponse(reviewId, "대충 리뷰 수정함", "대충 내용 수정함", 5, fixedDate2, bookId, memberId);
//
//        // given
//        MockMultipartFile reviewPart = new MockMultipartFile(
//                "review",
//                "",
//                "application/json",
//                objectMapper.writeValueAsBytes(request2)
//        );
//        given(reviewService.updateReview(eq(bookId), eq(reviewId), any(ReviewRequest.class), eq(memberId), isNull())).willReturn(response2);
//
//        // when & then
//        mockMvc.perform(multipart("/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
//                    .file(reviewPart)
//                    .header("X-USER-ID", memberId)
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.reviewId").value(reviewId))
//                .andExpect(jsonPath("$.reviewTitle").value("대충 리뷰 수정함"))
//                .andExpect(jsonPath("$.reviewContent").value("대충 내용 수정함"))
//                .andExpect(jsonPath("$.reviewRating").value(5))
//                .andExpect(jsonPath("$.reviewDate").value(fixedDate2.toString()))
//                .andExpect(jsonPath("$.bookId").value(bookId))
//                .andExpect(jsonPath("$.memberId").value(memberId));
//
//        verify(reviewService).updateReview(eq(bookId), eq(reviewId), any(ReviewRequest.class), eq(memberId), isNull());
    }

    // TODO: 수정시 memberId 불일치 case 추가
}
