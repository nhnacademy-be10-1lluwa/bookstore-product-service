package com.nhnacademy.illuwa.d_book.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookExternalResponse {

    private String title;
    private String description;
    private String author;
    private String publisher;
    private LocalDate pubDate;
    private String isbn;
    private Integer priceStandard;
    private Integer priceSales;
    //이미지 url
    private String cover;
    private String categoryName;

}
