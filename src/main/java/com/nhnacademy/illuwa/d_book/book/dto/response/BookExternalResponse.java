package com.nhnacademy.illuwa.d_book.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "외부 도서 정보 응답 DTO")
public class BookExternalResponse {
    @Schema(description = "도서 제목", example = "어린 왕자")
    private String title;
    @Schema(description = "도서 설명", example = "어린 왕자와 장미꽃의 이야기")
    private String description;
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리")
    private String author;
    @Schema(description = "도서 출판사", example = "NHN출판")
    private String publisher;
    @Schema(description = "도서 출판일 (YYYY-MM-DD)", example = "2023-01-01")
    private LocalDate pubDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;
    @Schema(description = "도서 정가", example = "15000")
    private Integer priceStandard;
    @Schema(description = "도서 판매가", example = "13500")
    private Integer priceSales;
    @Schema(description = "도서 표지 이미지 URL", example = "http://example.com/cover.jpg")
    private String cover;
    @Schema(description = "카테고리 이름", example = "소설")
    private String categoryName;
}
