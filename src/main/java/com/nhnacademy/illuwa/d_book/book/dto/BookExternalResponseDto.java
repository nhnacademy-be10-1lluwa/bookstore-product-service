package com.nhnacademy.illuwa.d_book.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookExternalResponseDto {
    private String title;
    private String author;
    private String pubDate;
    private String description;
    private String isbn;
    private Integer priceSales;
    private Integer priceStandard;
    //이미지 url
    private String cover;
    private String categoryName;
    private String publisher;
}
