package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.*;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;

    @GetMapping("/external")
    public ResponseEntity<List<BookExternalResponse>> searchBooksByExternalApi(@RequestParam("title") String title){
        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);
        return ResponseEntity.ok(bookExternalResponses);
    }

    @PostMapping(value = "/register/manual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerBookDirectly(
            @ModelAttribute BookRegisterRequest request) {

        bookService.registgerBookDirectly(request, request.getImageFile());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 외부 API 사용
    @PostMapping("register/aladin")
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

    @PostMapping(value = "/register/api")
    public ResponseEntity<Void> registerBookByAladin(
            @RequestBody FinalAladinBookRegisterRequest request) {

        bookService.registerBookByAladin(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
