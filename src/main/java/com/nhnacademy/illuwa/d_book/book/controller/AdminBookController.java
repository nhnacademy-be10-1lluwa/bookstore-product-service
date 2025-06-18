package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.BookSearchRequest;
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

    @GetMapping
    public ResponseEntity<List<BookExternalResponse>> searchBook(@RequestParam String title){

        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);

        return ResponseEntity.ok(bookExternalResponses);
    }

    @PostMapping()
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookRegisterRequest req){

        BookDetailResponse detailResponse = bookService.registerBook(req.getIsbn());

        return ResponseEntity.ok(detailResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBook(String isbn){

        bookService.deleteBookByIsbn(isbn);

        return ResponseEntity.noContent().build();
    }
}
