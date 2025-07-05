package com.nhnacademy.illuwa.d_book.book.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "books-v1") // 인덱스 이름 (운영에서는 버전 관리를 위해 v1 등을 붙이는 것이 좋습니다)
@Setting(settingPath = "elasticsearch/nori-settings.json") // nori 분석기 설정 파일 경로
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String description;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String author;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Integer)
    private int salePrice;

    @Field(type = FieldType.Text)
    private String thumbnailUrl;
}

