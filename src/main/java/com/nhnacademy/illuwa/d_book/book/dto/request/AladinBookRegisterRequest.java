package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알라딘 도서 등록 요청 DTO")
public class AladinBookRegisterRequest {
    @Schema(description = "도서 제목")
    private String title;
    @Schema(description = "저자")
    private String author;
    @Schema(description = "출판사")
    private String publisher;
    @Schema(description = "도서 설명")
    private String description;
    @Schema(description = "출판일")
    private String pubDate;
    @Schema(description = "ISBN")
    private String isbn;
    @Schema(description = "정가")
    private int regularPrice;
    @Schema(description = "판매 가격")
    private int salePrice;
    @Schema(description = "표지 이미지 URL")
    private String cover;
    @Schema(description = "카테고리 이름")
    private String categoryName;
}