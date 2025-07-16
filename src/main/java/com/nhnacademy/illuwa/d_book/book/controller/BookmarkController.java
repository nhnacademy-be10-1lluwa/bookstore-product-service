package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookmarkResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.d_book.book.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/books/{isbn}/bookmarks")
public class BookmarkController {
    private final BookService bookService;
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<BookmarkResponse> toggleBookmark(@PathVariable String isbn,
                                                           @RequestHeader("X-USER-ID") Long memberId) {

        Optional<BookDetailResponse> bookInfo = bookService.getAllBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();

        if (bookInfo.isPresent()) {
            long bookId = bookInfo.get().getId();
            return ResponseEntity.ok(bookmarkService.toggleBookmark(bookId, memberId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}