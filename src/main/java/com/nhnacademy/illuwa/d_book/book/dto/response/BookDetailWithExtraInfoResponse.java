package com.nhnacademy.illuwa.d_book.book.dto.response;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "도서 상세 정보 및 부가 정보 응답 DTO")
public class BookDetailWithExtraInfoResponse {

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
    private String publishedDate;
    @Schema(description = "도서 ISBN", example = "978896098335")
    private String isbn;
    @Schema(description = "도서 정가", example = "15000.00")
    private BigDecimal regularPrice;
    @Schema(description = "도서 판매가", example = "13500.00")
    private BigDecimal salePrice;
    @Schema(description = "도서 표지 이미지 URL", example = "http://example.com/cover.jpg")
    private String imgUrl;

    @Schema(description = "선물 포장 가능 여부", example = "true")
    private Boolean giftwrap;
    @Schema(description = "도서 재고 수량", example = "100")
    private Integer count;
    @Schema(description = "도서 상태 (NORMAL, DISCONTINUED, OUT_OF_STOCK, DELETED)", example = "NORMAL")
    private Status status;

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
    @Schema(description = "레벨 1 카테고리 ID (최상위 카테고리)", example = "1")
    private Long level1;
    @Schema(description = "레벨 2 카테고리 ID (중간 카테고리)", example = "2")
    private Long level2;
    @Schema(description = "레벨 1 카테고리 이름", example = "소설")
    private String level1Name;
    @Schema(description = "레벨 2 카테고리 이름", example = "판타지")
    private String level2Name;
    @Schema(description = "카테고리 이름", example = "SF")
    private String categoryName;
    @Schema(description = "도서 태그 목록")
    private List<TagResponse> tags;



    public BookDetailWithExtraInfoResponse toDto(Book book, BookCategory bookCategory, List<TagResponse> tags) {
        Category category = bookCategory.getCategory();

        String categoryName = category.getCategoryName();
        String level2Name = null;
        String level1Name = null;

        if (category.getParentCategory() != null) {
            level2Name = category.getParentCategory().getCategoryName();
            if (category.getParentCategory().getParentCategory() != null) {
                level1Name = category.getParentCategory().getParentCategory().getCategoryName();
            }
        }


        List<TagResponse> tagResponses = book.getBookTags() != null
                ? book.getBookTags().stream()
                .map(bt -> new TagResponse(bt.getTag().getId(), bt.getTag().getName()))
                .toList()
                : Collections.emptyList();

        Long categoryIdToUse = null;
        Long level1 = null;
        Long level2 = null;

        // 3단계 트리인지 체크
        if (category.getParentCategory() != null && category.getParentCategory().getParentCategory() != null) {
            categoryIdToUse = category.getId();
            level1 = category.getParentCategory().getParentCategory().getId();
            level2 = category.getParentCategory().getId();
        } else if (category.getParentCategory() != null) {
            categoryIdToUse = category.getId();
            level1 = category.getParentCategory().getId();
        } else {
            categoryIdToUse = category.getId();
        }

        return BookDetailWithExtraInfoResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate().toString())
                .isbn(book.getIsbn())
                .regularPrice(book.getRegularPrice())
                .salePrice(book.getSalePrice())
                .imgUrl(book.getBookImages().isEmpty() ? null : book.getBookImages().get(0).getImageUrl())
                .giftwrap(Optional.ofNullable(book.getBookExtraInfo())
                        .map(info -> info.getGiftWrap() != null ? info.getGiftWrap() : false)
                        .orElse(false))
                .count(book.getBookExtraInfo().getCount())
                .tags(tagResponses)
                .status(book.getBookExtraInfo().getStatus())
                .categoryId(categoryIdToUse)
                .level1(level1)
                .level2(level2)
                .categoryName(categoryName)
                .level1Name(level1Name)
                .level2Name(level2Name)
                .build();
    }

}