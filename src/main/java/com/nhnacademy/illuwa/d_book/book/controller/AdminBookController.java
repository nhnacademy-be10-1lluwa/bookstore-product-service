package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.*;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {

    BookService bookService;
    AladinBookApiService aladinBookApiService;
    BookMapper bookMapper;

    AdminBookController(BookService bookService, AladinBookApiService aladinBookApiService, BookMapper bookMapper){
        this.bookService = bookService;
        this.aladinBookApiService = aladinBookApiService;
        this.bookMapper = bookMapper;
    }

    @GetMapping("/external")
    public ResponseEntity<List<BookExternalResponse>> searchBooksByExternalApi(@RequestParam String title){
        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);
        return ResponseEntity.ok(bookExternalResponses);
    }

    // 도서 등록
    @PostMapping()
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookRegisterRequest bookRegisterRequest){
        BookDetailResponse detailResponse = bookService.registerBook(bookRegisterRequest);
        return ResponseEntity.ok(detailResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody BookUpdateRequest requestDto){
        bookService.updateBook(id,requestDto);
        return ResponseEntity.noContent().build();
    }
}
