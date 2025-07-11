package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookApiRegisterRequest {

    private String title;
    private String author;
    private String publisher;
    private String contents;
    private String pubDate;
    private String isbn;
    private Integer regularPrice;
    private Integer salePrice;
    private String description;
    private String cover; // hidden input으로 받은 이미지 URL
    private int count;
    private Long categoryId;
}