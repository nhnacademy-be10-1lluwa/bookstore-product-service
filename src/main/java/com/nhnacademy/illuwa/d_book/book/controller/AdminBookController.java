package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.*;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 도서 직접 등록
    @PostMapping(value = "/direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDetailResponse> createBookDirectly(
            @RequestPart("bookInfo") BookRegisterRequest bookRegisterRequest,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        BookDetailResponse registeredBook = bookService.createBookDirectly(bookRegisterRequest, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredBook);
    }

    // 외부 API 사용
    @PostMapping()
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookRegisterRequest bookRegisterRequest){
        BookDetailResponse detailResponse = bookService.registerBook(bookRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
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
