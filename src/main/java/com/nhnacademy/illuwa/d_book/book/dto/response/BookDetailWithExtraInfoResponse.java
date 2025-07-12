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
    private String publishedDate;
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

    public BookDetailWithExtraInfoResponse toDto(Book book, BookCategory bookCategory) {
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
                .categoryId(category.getId())
                .level1(category.getParentCategory() != null && category.getParentCategory().getParentCategory() != null
                        ? category.getParentCategory().getParentCategory().getId() : null)
                .level2(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .level1Name(level1Name)
                .level2Name(level2Name)
                .categoryName(categoryName)
                .build();
    }

}