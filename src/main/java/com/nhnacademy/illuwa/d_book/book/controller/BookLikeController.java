package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-likes")
public class BookLikeController {
    private final BookLikeService bookLikeService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> isLikedByMe(@RequestParam long bookId,
                                               @RequestHeader("X-USER-ID") Long memberId){
        return ResponseEntity.ok(bookLikeService.isLikedByMe(bookId, memberId));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<SimpleBookResponse>> getLikedBooksByMember(
            @RequestParam("page") int page, @RequestParam("size") int size,
            @RequestHeader("X-USER-ID") Long memberId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLikeService.getLikedBooksByMember(memberId, pageable));
    }

    @PostMapping
    public ResponseEntity<Void> toggleBookLikes(@RequestParam("book-id") long bookId,
                                                            @RequestHeader("X-USER-ID") Long memberId) {
        bookLikeService.toggleBookLikes(bookId, memberId);
        return ResponseEntity.ok().build();
    }
}