package com.nhnacademy.illuwa.d_book.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 직접 등록 요청 DTO")
public class BookRegisterRequest {
    @Schema(description = "도서 제목", example = "어린 왕자")
    private String title;
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리")
    private String author;
    @Schema(description = "도서 출판사", example = "NHN출판")
    private String publisher;
    @Schema(description = "도서 목차", example = "1. 첫 만남\n2. 장미꽃")
    private String contents;
    @Schema(description = "도서 출판일 (YYYY-MM-DD)", example = "2023-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String pubDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;
    @Schema(description = "도서 정가", example = "15000")
    private int regularPrice;
    @Schema(description = "도서 판매가", example = "13500")
    private int salePrice;
    @Schema(description = "도서 설명", example = "어린 왕자와 장미꽃의 이야기")
    private String description;
    @Schema(description = "도서 표지 이미지 파일")
    private MultipartFile imageFile;
    @Schema(description = "도서 재고 수량", example = "100")
    private Integer count;
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;


    public LocalDate getParsedPubDate() {
        return LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
