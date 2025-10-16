package com.nhnacademy.illuwa.search.controller;

import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

//@CrossOrigin
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Book Search API", description = "도서 검색 관련 API")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @Operation(summary = "키워드로 도서 검색", description = "키워드를 사용하여 도서를 검색하고 페이징된 결과를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDocument.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<Page<BookDocument>> searchBooksByKeyword(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<BookDocument> results = bookSearchService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "도서 인덱싱", description = "새로운 도서 문서를 Elasticsearch에 인덱싱합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "도서가 성공적으로 인덱싱되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping()
    public ResponseEntity<Void> indexBook(@RequestBody BookDocument bookDocument) {
        bookSearchService.save(bookDocument);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "도서 인덱스 삭제", description = "도서 ID를 통해 Elasticsearch에서 도서 인덱스를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "도서 인덱스가 성공적으로 삭제되었습니다."),
        @ApiResponse(responseCode = "404", description = "도서 인덱스를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndex(@PathVariable Long id) {
        bookSearchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 도서 동기화", description = "데이터베이스의 모든 도서 정보를 Elasticsearch와 동기화합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "도서 정보가 성공적으로 동기화되었습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/_sync")
    public ResponseEntity<Void> syncAllBooks() {
        bookSearchService.syncAllBooksToElasticsearch();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리로 도서 검색", description = "카테고리를 사용하여 도서를 검색하고 페이징된 결과를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDocument.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/category")
    public ResponseEntity<Page<BookDocument>> searchBooksByCategory(@RequestParam String category, Pageable pageable) {
        Page<BookDocument> results = bookSearchService.searchByCategory(category, pageable);
        return ResponseEntity.ok(results);
    }



}