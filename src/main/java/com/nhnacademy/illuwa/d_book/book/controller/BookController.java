package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.*;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    BookService bookService;
    AladinBookApiService aladinBookApiService;
    BookMapper bookMapper;

    BookController(BookService bookService, AladinBookApiService aladinBookApiService, BookMapper bookMapper){
        this.bookService = bookService;
        this.aladinBookApiService = aladinBookApiService;
        this.bookMapper = bookMapper;
    }


    @GetMapping("/search")
    public ResponseEntity<List<BookDetailResponse>> searchBooksByTitle(@RequestParam String title){
        List<BookDetailResponse> bookDetailsResponses = bookService.searchBookByTitle(title);
        return ResponseEntity.ok(bookDetailsResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> searchBookById(@PathVariable Long id){
        BookDetailResponse bookDetailResponse = bookService.searchBookById(id);
        return ResponseEntity.ok(bookDetailResponse);
    }

    //등록된 도서 목록
    @GetMapping("/all")
    public ResponseEntity<List<BookDetailResponse>> getRegisteredBooks(){
        List<BookDetailResponse> registeredBooks = bookService.getAllBooks();
        return ResponseEntity.ok(registeredBooks);
    }

    //등록된 도서 목록
    @GetMapping()
    public ResponseEntity<Page<BookDetailResponse>> getRegisteredBooksByPaging(Pageable pageable){
        Page<BookDetailResponse> registeredBooks = bookService.getAllBooksByPaging(pageable);
        return ResponseEntity.ok(registeredBooks);
    }

    // ISBN으로 도서 검색(도서 클릭)
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookExternalResponse> searchBookByIsbn(@PathVariable String isbn){
        BookExternalResponse bookByIsbn = aladinBookApiService.findBookByIsbn(isbn);
        return ResponseEntity.ok(bookByIsbn);
    }

    @GetMapping("/bestseller")
    public ResponseEntity<List<BestSellerResponse>> getBestSeller(){
        List<BestSellerResponse> bestSellerList = aladinBookApiService.getBestSeller();
        return ResponseEntity.ok(bestSellerList);
    }

    @GetMapping("/search/criteria")
    public ResponseEntity<Page<BookDetailResponse>> searchBooksByCriteria(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tagName,
            Pageable pageable) {

        Page<BookDetailResponse> BooksByCriteria
                = bookService.searchBooksByCriteria(categoryId, tagName, pageable);
        return ResponseEntity.ok(BooksByCriteria);
    }




}
