package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 좋아요 상태를 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<Boolean> isLikedByMe(@RequestParam("book-id") long bookId,
                                               @RequestHeader("X-USER-ID") Long memberId){
        return ResponseEntity.ok(bookLikeService.isLikedByMe(bookId, memberId));
    }

    @Operation(summary = "도서 좋아요 설정하기 (토글)", description = "도서에 좋아요를 설정하거나 취소할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 좋아요 상태를 변경했습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Void> toggleBookLikes(@RequestParam("book-id") long bookId,
                                                @RequestHeader("X-USER-ID") Long memberId) {
        bookLikeService.toggleBookLikes(bookId, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 한 도서 가져오기 (페이징)", description = "현재 유저가 좋아요한 도서들을 가져옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 좋아요한 도서 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SimpleBookResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<Page<SimpleBookResponse>> getLikedBooksByMember(@RequestParam("page") int page,
                                                                          @RequestParam("size") int size,
                                                                          @RequestHeader("X-USER-ID") Long memberId) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLikeService.getLikedBooksByMember(memberId, pageable));
    }
}