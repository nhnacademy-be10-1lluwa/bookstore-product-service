package com.nhnacademy.illuwa.d_book.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalAladinBookRegisterRequest {
    private String title;
    private String author;
    private String publisher;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String pubDate;

    private String isbn;
    private int regularPrice;
    private int salePrice;
    private String description;
    private String contents;

    private Integer count; // 재고 수량 (null 허용)

    private Long categoryId;

    private String imageFileUrl; // 이미지 URL (hidden input으로 전달)
}