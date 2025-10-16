package com.nhnacademy.illuwa.d_book.book.dto.response;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "도서 상세 정보 및 추가 정보 응답 DTO")
public class BookDetailWithExtraInfoResponse {

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
    private String publishedDate; // 문자열로 유지
    @Schema(description = "ISBN")
    private String isbn;
    @Schema(description = "정가")
    private BigDecimal regularPrice;
    @Schema(description = "판매 가격")
    private BigDecimal salePrice;
    @Schema(description = "표지 이미지 URL")
    private String imgUrl;

    @Schema(description = "선물 포장 가능 여부")
    private Boolean giftwrap;
    @Schema(description = "재고 수량")
    private Integer count;
    @Schema(description = "도서 상태")
    private Status status;

    @Schema(description = "카테고리 ID")
    private Long categoryId;
    @Schema(description = "1단계 카테고리 ID")
    private Long level1;
    @Schema(description = "2단계 카테고리 ID")
    private Long level2;
    @Schema(description = "1단계 카테고리 이름")
    private String level1Name;
    @Schema(description = "2단계 카테고리 이름")
    private String level2Name;
    @Schema(description = "카테고리 이름")
    private String categoryName;
    @Schema(description = "태그 목록")
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