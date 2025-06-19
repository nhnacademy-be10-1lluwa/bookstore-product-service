package com.nhnacademy.illuwa.d_book.book.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookUpdateRequest {

    private String contents;

    private String description;

    private Integer price;

    private Boolean giftWrap;
}
