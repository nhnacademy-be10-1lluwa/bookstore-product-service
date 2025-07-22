package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external-books")
@RequiredArgsConstructor
@Slf4j
public class ExternalBookController {

    private final AladinBookApiService aladinBookApiService;

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookExternalResponse> getBookByIsbn(@PathVariable String isbn) {
        BookExternalResponse bookDetail = aladinBookApiService.findBookByIsbn(isbn);

        log.info("요청 받은 ISBN: {}", isbn);
        log.info("알라딘 API 결과: {}", bookDetail);

        if (bookDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookDetail);
    }
}
