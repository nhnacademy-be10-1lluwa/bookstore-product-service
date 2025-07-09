package com.nhnacademy.illuwa.d_book.book.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AladinBookRegisterRequest {
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String pubDate;
    private String isbn;
    private int regularPrice;
    private int salePrice;
    private String cover;
    private String categoryName;
}