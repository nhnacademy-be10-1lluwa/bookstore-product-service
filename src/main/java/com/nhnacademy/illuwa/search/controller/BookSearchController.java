package com.nhnacademy.illuwa.search.controller;

import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping
    public ResponseEntity<Page<BookDocument>> searchBooksByKeyword(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<BookDocument> results = bookSearchService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping()
    public ResponseEntity<Void> indexBook(@RequestBody BookDocument bookDocument) {
        bookSearchService.save(bookDocument);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndex(@PathVariable Long id) {
        bookSearchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/_sync")
    public ResponseEntity<Void> syncAllBooks() {
        bookSearchService.syncAllBooksToElasticsearch();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category")
    public ResponseEntity<Page<BookDocument>> searchBooksByCategory(@RequestParam String category, Pageable pageable) {
        Page<BookDocument> results = bookSearchService.searchByCategory(category, pageable);
        return ResponseEntity.ok(results);
    }



}