package com.nhnacademy.illuwa.d_book.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class BookDetailResponse {
    private final Long id;
    private final String title;
    private final String contents;
    private final String description;
    private final String author;
    private final String publisher;
    private final LocalDate publishedDate;
    private final String isbn;
    private final int regularPrice;
    private final int salePrice;
    private final boolean isGiftWrap;
    private final String imgUrl;
    private final String category;

    public BookDetailResponse(Long id, String title, String contents, String description, String author, String publisher, LocalDate publishedDate, String isbn, int regularPrice, int salePrice, boolean isGiftWrap, String imgUrl, String category) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isGiftWrap = isGiftWrap;
        this.imgUrl = imgUrl;
        this.category = category;
    }
}
