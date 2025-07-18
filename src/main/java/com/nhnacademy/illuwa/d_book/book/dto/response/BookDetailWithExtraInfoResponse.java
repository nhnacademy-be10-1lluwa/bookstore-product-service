package com.nhnacademy.illuwa.d_book.book.dto.response;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailWithExtraInfoResponse {

    private Long id;
    private String title;
    private String contents;
    private String description;
    private String author;
    private String publisher;
    private String publishedDate; // 문자열로 유지
    private String isbn;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private String imgUrl;

    private boolean giftwrap;
    private Integer count;
    private Status status;

    private Long categoryId;
    private Long level1;
    private Long level2;
    private String level1Name;
    private String level2Name;
    private String categoryName;
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
                .giftwrap(book.getBookExtraInfo().isGiftWrap())
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