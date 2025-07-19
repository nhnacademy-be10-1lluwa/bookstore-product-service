package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookLikeResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-likes")
public class BookLikeController {
    private final BookService bookService;
    private final BookLikeService bookmarkService;

    @PostMapping
    public ResponseEntity<BookLikeResponse> toggleBookLikes(@RequestParam long bookId,
                                                            @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(bookmarkService.toggleBookLikes(bookId, memberId));
    }

    // 좋아요한 도서목록 가져오기 추가
    @GetMapping("/api/book-likes/list")
    public ResponseEntity<List<BookDetailResponse>> getLikedBooksByMember(@RequestHeader Long memberId) {

        return ResponseEntity.ok(bookmarkService.getLikedBooksByMember(memberId));
    }
}