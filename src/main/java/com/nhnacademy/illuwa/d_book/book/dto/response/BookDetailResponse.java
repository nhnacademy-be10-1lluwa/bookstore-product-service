package com.nhnacademy.illuwa.d_book.book.dto.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 상세 응답 DTO")
public class BookDetailResponse {
    @Schema(description = "도서 ID")
    private Long id;
    @Schema(description = "도서 제목")
    private String title;
    @Schema(description = "도서 내용")
    private String contents;
    @Schema(description = "도서 설명")
    private String description;
    @Schema(description = "저자")
    private String author;
    @Schema(description = "출판사")
    private String publisher;
    @Schema(description = "출판일")
    private LocalDate publishedDate;
    @Schema(description = "ISBN")
    private String isbn;
    @Schema(description = "정가")
    private BigDecimal regularPrice;
    @Schema(description = "판매 가격")
    private BigDecimal salePrice;
    @Schema(description = "선물 포장 가능 여부")
    private Boolean giftWrap;
    @Schema(description = "이미지 URL 목록")
    private  List<String> imageUrls;
    @Schema(description = "재고 수량")
    private  Integer count;
    @Schema(description = "도서 상태")
    private  String status;
}
