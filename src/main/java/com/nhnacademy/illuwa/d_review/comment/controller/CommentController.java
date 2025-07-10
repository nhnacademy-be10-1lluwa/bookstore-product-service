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
@RequestMapping("/api/books/{bookId}/reviews/{reviewId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long bookId,
                                                         @PathVariable Long reviewId,
                                                         @RequestBody @Valid CommentRequest request,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        CommentResponse response = commentService.createComment(reviewId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentList(@PathVariable Long bookId,
                                                                @PathVariable Long reviewId) {

        List<CommentResponse> responseList = commentService.getCommentList(reviewId);
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long bookId,
                                                         @PathVariable Long reviewId,
                                                         @PathVariable Long commentId,
                                                         @RequestBody @Valid CommentRequest request,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        CommentResponse response = commentService.updateComment(reviewId, commentId, request, memberId);
        return ResponseEntity.ok(response);
    }
}