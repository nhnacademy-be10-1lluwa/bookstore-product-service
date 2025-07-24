package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 등록 요청 DTO")
public class BookRegisterRequest {

    @Schema(description = "도서 제목", example = "자바의 정석")
    private String title;
    @Schema(description = "저자", example = "남궁성")
    private String author;
    @Schema(description = "출판사", example = "도우출판")
    private String publisher;
    @Schema(description = "도서 내용", example = "자바 프로그래밍의 기본부터 심화까지")
    private String contents;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "출판일 (yyyy-MM-dd)", example = "2023-01-01")
    private String pubDate;
    @Schema(description = "ISBN", example = "9788900000000")
    private String isbn;
    @Schema(description = "정가", example = "30000")
    private int regularPrice;
    @Schema(description = "판매 가격", example = "27000")
    private int salePrice;
    @Schema(description = "도서 설명", example = "자바를 배우는 모든 이들을 위한 필독서")
    private String description;
    @Schema(description = "표지 이미지 파일")
    private MultipartFile imageFile;
    @Schema(description = "재고 수량", example = "100")
    private Integer count;
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;


    public LocalDate getParsedPubDate() {
        return LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
