package com.nhnacademy.illuwa.search.document;

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
public class BookDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd")
    private LocalDate publishedDate;

    @Field(type = FieldType.Double)
    private BigDecimal salePrice;

    @Field(type = FieldType.Text, index = false)
    private String thumbnailUrl;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    @Setter
    @Field(type = FieldType.Keyword)
    private List<String> tags;
}