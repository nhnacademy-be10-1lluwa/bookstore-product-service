package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookLikeResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-likes")
public class BookLikeController {
    private final BookLikeService bookmarkService;

    @PostMapping
    public ResponseEntity<BookLikeResponse> toggleBookLikes(@RequestParam long bookId,
                                                            @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(bookmarkService.toggleBookLikes(bookId, memberId));
    }
}