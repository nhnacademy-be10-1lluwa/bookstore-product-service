package com.nhnacademy.illuwa.d_book.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 상세 정보 응답 DTO")
public class BookDetailResponse {
    @Schema(description = "도서 ID", example = "1")
    private Long id;
    @Schema(description = "도서 제목", example = "어린 왕자")
    private String title;
    @Schema(description = "도서 목차", example = "1. 첫 만남\n2. 장미꽃")
    private String contents;
    @Schema(description = "도서 설명", example = "어린 왕자와 장미꽃의 이야기")
    private String description;
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리")
    private String author;
    @Schema(description = "도서 출판사", example = "NHN출판")
    private String publisher;
    @Schema(description = "도서 출판일", example = "2023-01-01")
    private LocalDate publishedDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;
    @Schema(description = "도서 정가", example = "15000.00")
    private BigDecimal regularPrice;
    @Schema(description = "도서 판매가", example = "13500.00")
    private BigDecimal salePrice;
    @Schema(description = "선물 포장 가능 여부", example = "true")
    private Boolean giftWrap;
    @Schema(description = "도서 이미지 URL 목록")
    private List<String> imageUrls;
    @Schema(description = "도서 재고 수량", example = "100")
    private Integer count;
    @Schema(description = "도서 상태 (NORMAL, DISCONTINUED, OUT_OF_STOCK, DELETED)", example = "NORMAL")
    private String status;
}
