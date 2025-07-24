package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "API를 통한 도서 등록 요청 DTO")
public class BookApiRegisterRequest {

    @Schema(description = "도서 제목", example = "자바의 정석")
    private String title;
    @Schema(description = "저자", example = "남궁성")
    private String author;
    @Schema(description = "출판사", example = "도우출판")
    private String publisher;
    @Schema(description = "도서 내용", example = "자바 프로그래밍의 기본부터 심화까지")
    private String contents;
    @Schema(description = "출판일 (yyyy-MM-dd)", example = "2023-01-01")
    private String pubDate;
    @Schema(description = "ISBN", example = "9788900000000")
    private String isbn;
    @Schema(description = "정가", example = "30000")
    private Integer regularPrice;
    @Schema(description = "판매 가격", example = "27000")
    private Integer salePrice;
    @Schema(description = "도서 설명", example = "자바를 배우는 모든 이들을 위한 필독서")
    private String description;
    @Schema(description = "표지 이미지 URL", example = "http://example.com/cover.jpg")
    private String cover; // hidden input으로 받은 이미지 URL
    @Schema(description = "재고 수량", example = "100")
    private int count;
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
}