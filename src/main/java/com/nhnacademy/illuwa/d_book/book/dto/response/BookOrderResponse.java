package com.nhnacademy.illuwa.d_book.book.dto.response;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BookOrderResponse {

    private Long id;
    private String title;
    private String author;
    private BigDecimal salePrice;

    private Integer stockCount;
    private String status;
    private boolean isGiftWrapAvailable;

    public static BookOrderResponse fromEntity(Book book) {
        Integer stockCount = 0;
        String status = Status.NORMAL.toString();
        boolean isGiftWrapAvailable = false;

        if (book.getBookExtraInfo() != null) {
            stockCount = book.getBookExtraInfo().getCount();
            status = book.getBookExtraInfo().getStatus().name();
            isGiftWrapAvailable = book.getBookExtraInfo().isGiftwrap();
        }

        return BookOrderResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .salePrice(book.getSalePrice())
                .stockCount(stockCount)
                .status(status)
                .isGiftWrapAvailable(isGiftWrapAvailable)
                .build();
    }
}
