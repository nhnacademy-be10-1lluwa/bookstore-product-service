package com.nhnacademy.illuwa.d_review.comment.controller;

import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews/{reviewId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long bookId, @PathVariable Long reviewId, @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(reviewId, request));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentList(@PathVariable Long bookId, @PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentList(reviewId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long bookId, @PathVariable Long reviewId, @PathVariable Long commentId, @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(reviewId, commentId, request));
    }
}