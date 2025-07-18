package com.nhnacademy.illuwa.d_review.comment.controller;

import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/reviews/{review-id}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable(name = "review-id") Long reviewId,
                                                         @RequestBody @Valid CommentRequest request,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        CommentResponse response = commentService.createComment(reviewId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/public/reviews/{review-id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentList(@PathVariable(name = "review-id") Long reviewId) {

        List<CommentResponse> responseList = commentService.getCommentList(reviewId);
        return ResponseEntity.ok(responseList);
    }

    // 프론트에서 feign 으로 수정요청 받으려면 어쩔수 없이 Post 써야함 (feign 은 patch 미지원)
    @PostMapping("/api/reviews/{review-id}/comments/{comment-id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable(name = "review-id") Long reviewId,
                                                         @PathVariable(name = "comment-id") Long commentId,
                                                         @RequestBody @Valid CommentRequest request,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        CommentResponse response = commentService.updateComment(reviewId, commentId, request, memberId);
        return ResponseEntity.ok(response);
    }
}