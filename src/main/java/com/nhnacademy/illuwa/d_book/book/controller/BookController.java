package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import io.swagger.v3.oas.annotations.Operation;
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



    // 도서 상세 정보 조회 (ISBN - 고유 식별자이므로 경로 변수로 처리)
    @Operation(summary = "ISBN으로 DB 도서 검색", description = "DB에서 ISBN으로 등록된 도서 검색")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 정보를 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDetailResponse> getBookByIsbn(@PathVariable String isbn){ // 메소드명 명확화
        BookDetailResponse bookByIsbn = bookService.searchBookByIsbn(isbn);
        return ResponseEntity.ok(bookByIsbn);
    }


    @Operation(summary = "도서 목록 조회 및 검색 (통합)", description = "도서 목록을 조회하거나, 베스트셀러 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 목록을 반환합니다.",
            content = {
                @Content(mediaType = "application/json",
                    schema = @Schema(oneOf = {BookDetailResponse.class, BestSellerResponse.class}))
            }),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(name = "type", required = false) String type,
            Pageable pageable) {

        if ("bestseller".equalsIgnoreCase(type)) {
            List<BestSellerResponse> bestSellerList = aladinBookApiService.getBestSeller();
            return ResponseEntity.ok(bestSellerList);
        }

        Page<BookDetailResponse> allBooksPaged = bookService.getAllBooksByPaging(pageable);
        return ResponseEntity.ok(allBooksPaged);
    }


    // GET /api/books/{bookId}
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



//      <ExternalBookController로 이동>
//    @GetMapping("/external/isbn/{isbn}")
//    public ResponseEntity<BookExternalResponse> getBookByIsbnFromExternalApi(@PathVariable String isbn){
//        BookExternalResponse bookDetail = aladinBookApiService.findBookByIsbn(isbn);
//
//        if (bookDetail == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(bookDetail);
//    }

}
