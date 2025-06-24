package com.nhnacademy.illuwa.d_review.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.comment.controller.CommentController;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.service.CommentService;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Book book = new Book(
            10L,
            "이상한 책",
            "목차",
            "아무 설명",
            "이상한 사람",
            "무명출판",
            LocalDate.of(1970, 1, 1),
            "11111111111111111",
            99999999,
            99999999,
            null,
            new BookExtraInfo(Status.OUT_OF_STOCK,false)
    );

    private final Review review = new Review(
            3L,
            "리뷰임",
            "내용없음",
            null,
            5,
            LocalDateTime.of(2024, 12, 31, 23, 59, 59),
            book,
            1111L
    );

    private final Long commentId = 1L;
    private final Long memberId = 77L;
    private final LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 1, 1, 1);

    private CommentRequest request;
    private CommentResponse response;

    @BeforeEach
    void setUp() {
        request = new CommentRequest("ㄹㅇㅋㅋ");
        response = new CommentResponse(commentId, "ㄹㅇㅋㅋ", fixedTime, review.getReviewId(), memberId);
    }

    @Test
    @DisplayName("댓글 작성")
    void createCommentTest() throws Exception {
        // given
        given(commentService.createComment(eq(review.getReviewId()), any(CommentRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/books/{bookId}/reviews/{reviewId}/comments", book.getId(), review.getReviewId())
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.commentContents").value("ㄹㅇㅋㅋ"))
                .andExpect(jsonPath("$.commentDate").value(fixedTime.toString()))
                .andExpect(jsonPath("$.reviewId").value(3L))
                .andExpect(jsonPath("$.memberId").value(77L));

        verify(commentService).createComment(eq(review.getReviewId()), any(CommentRequest.class));
    }

    @Test
    @DisplayName("댓글 목록 가져오기")
    void getCommentListTest() throws Exception {
        // 댓글 목록 설정
        Long commentId2 = 2L;
        Long memberId2 = 66L;
        LocalDateTime fixedTime2 = fixedTime.plusDays(1);
        CommentResponse response2 = new CommentResponse(commentId2, "ㅁㄴㅇㄹ", fixedTime2, review.getReviewId(), memberId2);
        List<CommentResponse> responseList = Arrays.asList(response, response2);

        // given
        given(commentService.getCommentList(eq(review.getReviewId()))).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}/comments", book.getId(), review.getReviewId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].commentId").value(1L))
                .andExpect(jsonPath("$[0].commentContents").value("ㄹㅇㅋㅋ"))
                .andExpect(jsonPath("$[0].commentDate").value(fixedTime.toString()))
                .andExpect(jsonPath("$[0].reviewId").value(3L))
                .andExpect(jsonPath("$[0].memberId").value(77L))
                .andExpect(jsonPath("$[1].commentId").value(2L))
                .andExpect(jsonPath("$[1].commentContents").value("ㅁㄴㅇㄹ"))
                .andExpect(jsonPath("$[1].commentDate").value(fixedTime2.toString()))
                .andExpect(jsonPath("$[1].reviewId").value(3L))
                .andExpect(jsonPath("$[1].memberId").value(66L));

        verify(commentService).getCommentList(eq(review.getReviewId()));
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() throws Exception {
        LocalDateTime fixedTime2 = fixedTime.plusDays(1);
        CommentResponse response2 = new CommentResponse(commentId, "메롱메롱", fixedTime2, review.getReviewId(), memberId);

        // given
        given(commentService.updateComment(eq(review.getReviewId()), eq(commentId), any(CommentRequest.class))).willReturn(response2);

        // when & then
        mockMvc.perform(patch("/books/{bookId}/reviews/{reviewId}/comments/{commentId}", book.getId(), review.getReviewId(), commentId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.commentContents").value("메롱메롱"))
                .andExpect(jsonPath("$.commentDate").value(fixedTime2.toString()))
                .andExpect(jsonPath("$.reviewId").value(3L))
                .andExpect(jsonPath("$.memberId").value(77L));

        verify(commentService).updateComment(eq(review.getReviewId()), eq(commentId), any(CommentRequest.class));
    }
}
