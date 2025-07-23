package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "도서 좋아요 상태표시", description = "유저가 도서에 좋아요 했는지 표시합니다.")
    @GetMapping
    public ResponseEntity<Boolean> isLikedByMe(@RequestParam("book-id") long bookId,
                                               @RequestHeader("X-USER-ID") Long memberId){
        return ResponseEntity.ok(bookLikeService.isLikedByMe(bookId, memberId));
    }

    @Operation(summary = "도서 좋아요 설정하기 (토글)", description = "도서에 좋아요를 설정하거나 취소할 수 있습니다.")
    @PostMapping
    public ResponseEntity<Void> toggleBookLikes(@RequestParam("book-id") long bookId,
                                                @RequestHeader("X-USER-ID") Long memberId) {
        bookLikeService.toggleBookLikes(bookId, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 한 도서 가져오기 (페이징)", description = "현재 유저가 좋아요한 도서들을 가져옵니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<SimpleBookResponse>> getLikedBooksByMember(@RequestParam("page") int page,
                                                                          @RequestParam("size") int size,
                                                                          @RequestHeader("X-USER-ID") Long memberId) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLikeService.getLikedBooksByMember(memberId, pageable));
    }
}