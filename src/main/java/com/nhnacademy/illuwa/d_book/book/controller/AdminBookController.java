package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponseDto;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.BookSearchRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {
    BookService bookService;

    AdminBookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<List<BookExternalResponse>> searchBook(@RequestBody BookSearchRequest req){

        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(req.getTitle());

        return ResponseEntity.ok(bookExternalResponses);
    }

    @PostMapping("/register")
    public ResponseEntity<BookDetailResponseDto> registerBook(@RequestBody BookRegisterRequest req){

        BookDetailResponseDto detailResponse = bookService.registerBook(req.getISBN());

        return ResponseEntity.ok(detailResponse);
    }
}
