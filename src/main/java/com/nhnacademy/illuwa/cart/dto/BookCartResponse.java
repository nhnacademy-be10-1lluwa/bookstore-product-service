package com.nhnacademy.illuwa.cart.dto;

import com.nhnacademy.illuwa.cart.entity.BookCart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCartResponse {
    private Long bookId;
    private String title;
    private int amount;

    private int salePrice;
    private String imgUrl;

    public BookCartResponse(BookCart bookCart) {
        this.bookId = bookCart.getBook().getId();
        this.title = bookCart.getBook().getTitle();
        this.amount = bookCart.getAmount();
        this.salePrice = bookCart.getBook().getSalePrice();
        this.imgUrl = bookCart.getBook().getBookImages().stream()
                .findFirst()
                .map(bookImage -> bookImage.getImageUrl())
                .orElse(null);
    }
}
