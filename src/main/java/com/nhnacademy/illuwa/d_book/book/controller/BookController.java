package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
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

import java.util.List;

@Tag(name = "도서 API", description = "도서 조회 및 검색 관련 API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AladinBookApiService aladinBookApiService;



    @Operation(summary = "ISBN으로 DB 도서 검색", description = "DB에서 ISBN으로 등록된 도서 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 검색 성공",
                    content = @Content(schema = @Schema(implementation = BookDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDetailResponse> getBookByIsbn(@Parameter(description = "도서 ISBN", required = true) @PathVariable String isbn){ // 메소드명 명확화
        BookDetailResponse bookByIsbn = bookService.searchBookByIsbn(isbn);
        return ResponseEntity.ok(bookByIsbn);
    }


    @Operation(summary = "도서 목록 조회 및 검색 (통합)", description = "베스트셀러 또는 페이징된 도서 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공",
                    content = @Content(schema = @Schema(oneOf = {BestSellerResponse.class, Page.class})))
    })
    @GetMapping
    public ResponseEntity<?> getBooks(
            @Parameter(description = "조회할 도서 목록 타입 (bestseller)", example = "bestseller") @RequestParam(name = "type", required = false) String type,
            Pageable pageable) {

        if ("bestseller".equalsIgnoreCase(type)) {
            List<BestSellerResponse> bestSellerList = aladinBookApiService.getBestSeller();
            return ResponseEntity.ok(bestSellerList);
        }

        Page<BookDetailResponse> allBooksPaged = bookService.getAllBooksByPaging(pageable);
        return ResponseEntity.ok(allBooksPaged);
    }


    @Operation(summary = "도서 상세 정보 조회", description = "도서 ID로 특정 도서의 상세 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BookDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서 ID", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> searchBookById(@Parameter(description = "조회할 도서 ID", required = true) @PathVariable Long id){
        BookDetailResponse bookDetailResponse = bookService.searchBookById(id);
        return ResponseEntity.ok(bookDetailResponse);
    }
}
