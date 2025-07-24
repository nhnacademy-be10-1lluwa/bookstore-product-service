package com.nhnacademy.illuwa.d_book.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 정보 업데이트 요청 DTO")
public class BookUpdateRequest {
    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Schema(description = "도서 제목", example = "어린 왕자 (수정)")
    private String title;
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리 (수정)")
    private String author;
    @Schema(description = "도서 출판사", example = "NHN출판 (수정)")
    private String publisher;
    @Schema(description = "도서 출판일 (YYYY-MM-DD)", example = "2023-01-01")
    private String pubDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;

    @Schema(description = "도서 정가", example = "16000.00")
    private BigDecimal regularPrice;
    @Schema(description = "도서 판매가", example = "14000.00")
    private BigDecimal salePrice;

    @Schema(description = "도서 설명", example = "어린 왕자와 장미꽃의 이야기 (수정)")
    private String description;
    @Schema(description = "도서 목차", example = "1. 첫 만남 (수정)\n2. 장미꽃 (수정)")
    private String contents;

    @Schema(description = "도서 표지 이미지 URL", example = "http://example.com/new_cover.jpg")
    private String cover;

    @Schema(description = "도서 재고 수량", example = "90")
    private Integer count;

    @Schema(description = "도서 상태 (NORMAL, DISCONTINUED, OUT_OF_STOCK, DELETED)", example = "NORMAL")
    private String status;

    @Schema(description = "선물 포장 가능 여부", example = "true")
    private Boolean giftwrap;

    @Schema(description = "레벨 1 카테고리 ID", example = "1")
    private Long level1;
    @Schema(description = "레벨 2 카테고리 ID", example = "3")
    private Long level2;
    @Schema(description = "카테고리 ID", example = "2")
    private Long categoryId;
}