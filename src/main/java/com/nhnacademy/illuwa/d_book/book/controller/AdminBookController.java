package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.*;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {
    BookService bookService;

    AdminBookController(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping("/external")
    public ResponseEntity<List<BookExternalResponse>> searchBooksByExternalApi(@RequestParam String title){

        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);

        return ResponseEntity.ok(bookExternalResponses);
    }

    @GetMapping
    public ResponseEntity<List<BookDetailResponse>> searchBooksByTitle(@RequestParam String title){
        List<BookDetailResponse> bookDetailsResponses = bookService.searchBookByTitle(title);
        return ResponseEntity.ok(bookDetailsResponses);
    }

    @PostMapping()
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookRegisterRequest reqestDto){

        BookDetailResponse detailResponse = bookService.registerBook(reqestDto.getIsbn());

        return ResponseEntity.ok(detailResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBook(@RequestParam String isbn){

        bookService.deleteBookByIsbn(isbn);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{isbn}")
    public ResponseEntity<Void> updateBook(@PathVariable String isbn, @RequestBody BookUpdateRequest requestDto){

        bookService.updateBook(isbn,requestDto);

        return ResponseEntity.noContent().build();
    }
}
