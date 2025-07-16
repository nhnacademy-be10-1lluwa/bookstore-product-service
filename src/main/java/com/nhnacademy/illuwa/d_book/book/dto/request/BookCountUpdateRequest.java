package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookCountUpdateRequest {
    private Long bookId;
    private Integer bookCount;
}
