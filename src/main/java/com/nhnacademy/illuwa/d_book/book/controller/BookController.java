package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "도서 API", description = "도서 조회 및 검색 관련 API")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookSearchService bookSearchService;
    private final BookService bookService;
    private final AladinBookApiService aladinBookApiService;
    private final BookMapper bookMapper;

    BookController(BookService bookService, AladinBookApiService aladinBookApiService, BookMapper bookMapper, BookSearchService bookSearchService){
        this.bookService = bookService;
        this.aladinBookApiService = aladinBookApiService;
        this.bookMapper = bookMapper;
        this.bookSearchService = bookSearchService;
    }

    @Operation(summary = "도서 제목으로 검색", description = "DB에 저장된 도서를 제목(일부 포함)으로 검색")
    @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = BookDetailResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<List<BookDetailResponse>> searchBooksByTitle(@RequestParam String title){
        List<BookDetailResponse> bookDetailsResponses = bookService.searchBookByTitle(title);
        return ResponseEntity.ok(bookDetailsResponses);
    }

    @Operation(summary = "도서 상세 정보 조회", description = "도서 ID로 특정 도서의 상세 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BookDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서 ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> searchBookById(@PathVariable Long id){
        BookDetailResponse bookDetailResponse = bookService.searchBookById(id);
        return ResponseEntity.ok(bookDetailResponse);
    }

    @Operation(summary = "등록된 모든 도서 목록 조회", description = "페이징 없이 DB에 등록된 모든 도서 목록을 조회")
    @GetMapping()
    public ResponseEntity<List<BookDetailResponse>> getRegisteredBooks(){
        List<BookDetailResponse> registeredBooks = bookService.getAllBooks();
        return ResponseEntity.ok(registeredBooks);
    }


    @Operation(summary = "등록된 도서 목록 페이징 조회", description = "DB에 등록된 도서 목록을 페이징하여 조회")
    @GetMapping("/list")
    public ResponseEntity<Page<BookDetailResponse>> getRegisteredBooksByPaging(Pageable pageable){
        Page<BookDetailResponse> registeredBooks = bookService.getAllBooksByPaging(pageable);
        return ResponseEntity.ok(registeredBooks);
    }

    @Operation(summary = "베스트셀러 목록 조회", description = "알라딘 API를 사용하여 베스트셀러 목록을 조회")
    @GetMapping("/bestseller")
    public ResponseEntity<List<BestSellerResponse>> getBestSeller(){
        List<BestSellerResponse> bestSellerList = aladinBookApiService.getBestSeller();
        return ResponseEntity.ok(bestSellerList);
    }

    @Operation(summary = "조건부 도서 검색 (QueryDSL)", description = "카테고리 ID 또는 태그 이름으로 도서를 필터링하여 검색")
    @GetMapping("/search/criteria")
    public ResponseEntity<Page<BookDetailResponse>> searchBooksByCriteria(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tagName,
            Pageable pageable) {

        Page<BookDetailResponse> BooksByCriteria
                = bookService.searchBooksByCriteria(categoryId, tagName, pageable);
        return ResponseEntity.ok(BooksByCriteria);
    }

    @Operation(summary = "ISBN으로 DB 도서 검색", description = "DB에서 ISBN으로 등록된 도서 검색")
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDetailResponse> searchBookByIsbnInDB(@PathVariable String isbn){
        BookDetailResponse bookByIsbn = bookService.searchBookByIsbn(isbn);
        return ResponseEntity.ok(bookByIsbn);
    }

    @GetMapping("/external/isbn/{isbn}")
    public ResponseEntity<BookExternalResponse> getBookByIsbnFromExternalApi(@PathVariable String isbn){
        BookExternalResponse bookDetail = aladinBookApiService.findBookByIsbn(isbn);

        if (bookDetail == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bookDetail);
    }


}
