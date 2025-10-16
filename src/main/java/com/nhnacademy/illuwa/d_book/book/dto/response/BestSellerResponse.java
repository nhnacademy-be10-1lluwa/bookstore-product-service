package com.nhnacademy.illuwa.d_book.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "베스트셀러 응답 DTO")
public class BestSellerResponse {
    @Schema(description = "도서 제목")
    private String title;
    @Schema(description = "저자")
    private String author;
    @Schema(description = "출판사")
    private String publisher;
    @Schema(description = "도서 설명")
    private String description;
    @Schema(description = "ISBN")
    private String isbn;
    @Schema(description = "판매 가격")
    private int priceSales;
    @Schema(description = "정가")
    private int priceStandard;
    @Schema(description = "표지 이미지 URL")
    private String cover;
}
