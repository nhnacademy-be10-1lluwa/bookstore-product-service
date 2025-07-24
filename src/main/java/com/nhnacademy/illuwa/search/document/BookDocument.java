package com.nhnacademy.illuwa.search.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Document(indexName = "books-v2")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Elasticsearch 도서 문서 DTO")
public class BookDocument {
    @Id
    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Field(type = FieldType.Text)
    @Schema(description = "도서 제목", example = "어린 왕자")
    private String title;

    @Field(type = FieldType.Text)
    @Schema(description = "도서 설명", example = "어린 왕자와 장미꽃의 이야기")
    private String description;

    @Field(type = FieldType.Text)
    @Schema(description = "도서 저자", example = "앙투안 드 생텍쥐페리")
    private String author;

    @Field(type = FieldType.Keyword)
    @Schema(description = "도서 출판사", example = "NHN출판")
    private String publisher;

    @Field(type = FieldType.Keyword)
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd")
    @Schema(description = "도서 출판일", example = "2023-01-01")
    private LocalDate publishedDate;

    @Field(type = FieldType.Double)
    @Schema(description = "도서 판매가", example = "13500.00")
    private BigDecimal salePrice;

    @Field(type = FieldType.Text, index = false)
    @Schema(description = "도서 표지 이미지 URL", example = "http://example.com/cover.jpg")
    private String thumbnailUrl;

    @Field(type = FieldType.Keyword)
    @Schema(description = "카테고리 목록", example = "[\"소설\", \"판타지\"]")
    private List<String> categories;

    @Setter
    @Field(type = FieldType.Keyword)
    @Schema(description = "태그 목록", example = "[\"추천\", \"인기\"]")
    private List<String> tags;
}