package com.nhnacademy.illuwa.d_book.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "외부 API 도서 응답 DTO")
public class BookExternalResponse {
    @Schema(description = "도서 제목")
    private String title;
    @Schema(description = "도서 설명")
    private String description;
    @Schema(description = "저자")
    private String author;
    @Schema(description = "출판사")
    private String publisher;
    @Schema(description = "출판일")
    private LocalDate pubDate;
    @Schema(description = "ISBN")
    private String isbn;
    @Schema(description = "정가")
    private Integer priceStandard;
    @Schema(description = "판매가")
    private Integer priceSales;
    @Schema(description = "표지 이미지 URL")
    private String cover;
    @Schema(description = "카테고리 이름")
    private String categoryName;
}
