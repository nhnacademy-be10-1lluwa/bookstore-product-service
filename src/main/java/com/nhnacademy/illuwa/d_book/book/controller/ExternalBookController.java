package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "외부 도서 API", description = "외부 도서 정보 조회 관련 API")
@RestController
@RequestMapping("/api/external-books")
@RequiredArgsConstructor
@Slf4j
public class ExternalBookController {

    private final AladinBookApiService aladinBookApiService;

    @Operation(summary = "ISBN으로 외부 도서 조회", description = "외부 API를 통해 ISBN으로 도서 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookExternalResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookExternalResponse> getBookByIsbn(@Parameter(description = "도서 ISBN", required = true) @PathVariable String isbn) {
        BookExternalResponse bookDetail = aladinBookApiService.findBookByIsbn(isbn);

        log.info("요청 받은 ISBN: {}", isbn);
        log.info("알라딘 API 결과: {}", bookDetail);

        if (bookDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookDetail);
    }
}
