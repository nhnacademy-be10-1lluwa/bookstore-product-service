package com.nhnacademy.illuwa.d_book.book.dto.response;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import lombok.*;

import java.math.BigDecimal;

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

    public BookDetailWithExtraInfoResponse toDto(Book book, BookCategory bookCategory) {
        Category category = bookCategory.getCategory();

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
                .giftwrap(book.getBookExtraInfo().isGiftwrap())
                .count(book.getBookExtraInfo().getCount())
                .status(book.getBookExtraInfo().getStatus())
                .categoryId(categoryIdToUse)
                .level1(level1)
                .level2(level2)
                .build();
    }

}