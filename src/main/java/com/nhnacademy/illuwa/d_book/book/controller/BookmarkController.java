package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookmarkResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<BookmarkResponse> toggleBookmark(@RequestParam long bookId,
                                                           @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(bookmarkService.toggleBookmark(bookId, memberId));
    }
}