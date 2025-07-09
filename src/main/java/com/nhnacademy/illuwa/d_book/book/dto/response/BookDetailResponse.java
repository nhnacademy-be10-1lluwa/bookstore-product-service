package com.nhnacademy.illuwa.d_book.book.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
    private Long id;
    private String title;
    private String contents;
    private String description;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private String isbn;
    private int regularPrice;
    private int salePrice;
    private boolean isGiftWrap;
    private String imgUrl;
}
