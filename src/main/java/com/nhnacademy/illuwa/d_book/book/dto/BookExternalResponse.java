package com.nhnacademy.illuwa.d_book.book.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookExternalResponse {

    @JsonValue
    private String title;
    @JsonValue
    private String author;
    private LocalDate pubDate;
    private String description;
    private String isbn;
    private Integer priceSales;
    private Integer priceStandard;
    //이미지 url
    private String cover;
    private String categoryName;
    private String publisher;
}
