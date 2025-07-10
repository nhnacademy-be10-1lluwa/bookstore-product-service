package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateRequest {

    private String contents;

    private String description;

    private Integer price;

    private Boolean giftWrap;
}
