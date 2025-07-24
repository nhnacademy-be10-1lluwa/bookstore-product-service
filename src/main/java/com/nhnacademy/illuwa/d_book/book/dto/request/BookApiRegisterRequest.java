package com.nhnacademy.illuwa.d_book.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "외부 API를 통해 도서를 등록하기 위한 요청 DTO")
public class BookApiRegisterRequest {
    @Schema(description = "도서 제목", example = "어린 왕자")
    private String title;
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리")
    private String author;
    @Schema(description = "도서 출판사", example = "NHN출판")
    private String publisher;
    @Schema(description = "도서 출판일 (YYYY-MM-DD)", example = "2023-01-01")
    private String pubDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;
    @Schema(description = "도서 정가", example = "15000.00")
    private BigDecimal regularPrice;
    @Schema(description = "도서 판매가", example = "13500.00")
    private BigDecimal salePrice;
    @Schema(description = "도서 표지 이미지 URL", example = "http://example.com/cover.jpg")
    private String cover;
    @Schema(description = "도서 재고 수량", example = "100")
    private Integer count;
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
}
