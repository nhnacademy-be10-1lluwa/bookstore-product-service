package com.nhnacademy.illuwa.search.controller;

import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@Tag(name = "도서 검색 API", description = "Elasticsearch를 이용한 도서 검색 및 관리 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @Operation(summary = "키워드로 도서 검색", description = "Elasticsearch에서 키워드를 이용하여 도서를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 검색 성공",
                    content = @Content(schema = @Schema(implementation = BookDocument.class)))
    })
    @GetMapping
    public ResponseEntity<Page<BookDocument>> searchBooksByKeyword(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<BookDocument> results = bookSearchService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "도서 인덱싱", description = "단일 도서 문서를 Elasticsearch에 인덱싱합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 인덱싱 성공")
    })
    @PostMapping()
    public ResponseEntity<Void> indexBook(@RequestBody BookDocument bookDocument) {
        bookSearchService.save(bookDocument);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "도서 인덱스 삭제", description = "Elasticsearch에서 특정 도서의 인덱스를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "도서 인덱스 삭제 성공")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndex(@Parameter(description = "삭제할 도서 ID", required = true) @PathVariable Long id) {
        bookSearchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 도서 동기화", description = "데이터베이스의 모든 도서 정보를 Elasticsearch와 동기화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 도서 동기화 성공")
    })
    @PostMapping("/_sync")
    public ResponseEntity<Void> syncAllBooks() {
        bookSearchService.syncAllBooksToElasticsearch();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리로 도서 검색", description = "Elasticsearch에서 특정 카테고리에 속하는 도서를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 검색 성공",
                    content = @Content(schema = @Schema(implementation = BookDocument.class)))
    })
    @GetMapping("/category")
    public ResponseEntity<Page<BookDocument>> searchBooksByCategory(@Parameter(description = "검색할 카테고리", required = true) @RequestParam String category, Pageable pageable) {
        Page<BookDocument> results = bookSearchService.searchByCategory(category, pageable);
        return ResponseEntity.ok(results);
    }
}
