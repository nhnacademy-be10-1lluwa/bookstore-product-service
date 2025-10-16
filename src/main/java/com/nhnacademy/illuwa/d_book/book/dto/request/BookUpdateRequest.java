package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 업데이트 요청 DTO")
public class BookUpdateRequest {
    @Schema(description = "도서 ID")
    private Long id;

    @Schema(description = "도서 제목")
    private String title;
    @Schema(description = "저자")
    private String author;
    @Schema(description = "출판사")
    private String publisher;
    @Schema(description = "출판일")
    private String pubDate;
    @Schema(description = "ISBN")
    private String isbn;

    @Schema(description = "정가")
    private BigDecimal regularPrice;
    @Schema(description = "판매 가격")
    private BigDecimal salePrice;

    @Schema(description = "도서 설명")
    private String description;
    @Schema(description = "도서 내용")
    private String contents;

    @Schema(description = "표지 이미지 URL")
    private String cover;

    @Schema(description = "재고 수량")
    private Integer count;

    @Schema(description = "도서 상태")
    private String status;

    @Schema(description = "선물 포장 가능 여부")
    private Boolean giftwrap;

    @Schema(description = "1단계 카테고리 ID")
    private Long level1;
    @Schema(description = "2단계 카테고리 ID")
    private Long level2;
    @Schema(description = "카테고리 ID")
    private Long categoryId;
}