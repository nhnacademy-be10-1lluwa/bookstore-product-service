package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.cart.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailWithExtraInfoResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
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

    // 외부 API 사용 (front)
    @PostMapping("/register/aladin")
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookApiRegisterRequest bookApiRegisterRequest){
        // apiDTO -> bookRegisterDTO
        BookDetailResponse detailResponse = bookService.registerBookByApi(bookApiRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBookAndRelatedEntities(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Void> updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest requestDto
    ) {
        bookService.updateBook(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<BookDetailWithExtraInfoResponse> getBookDetailWithExtra(@PathVariable Long id) {
        BookDetailWithExtraInfoResponse response = bookService.getBookDetailWithExtraInfo(id);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/extra_info")
    public ResponseEntity<Page<BookDetailWithExtraInfoResponse>> getBooksWithExtraInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC; // 기본값

        if (sortParams.length > 1) {
            direction = Sort.Direction.fromString(sortParams[1]);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<BookDetailWithExtraInfoResponse> response = bookService.getAllBooksWithExtraInfo(pageable);

        return ResponseEntity.ok(response);
    }



    @PutMapping("/update/bookCount")
    public ResponseEntity<Void> deductBooksCount(
            @RequestBody List<BookCountUpdateRequest> requests
    ) {
        bookService.updateBooksCount(requests);
        return ResponseEntity.noContent().build();
    }







}
