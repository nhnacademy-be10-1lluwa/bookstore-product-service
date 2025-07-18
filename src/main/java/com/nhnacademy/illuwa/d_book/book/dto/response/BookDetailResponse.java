package com.nhnacademy.illuwa.d_book.book.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
    private Long id;
    private String title;
    private String contents;
    private String description;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private String isbn;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private boolean giftWrap;
    private  List<String> imageUrls;
    private  Integer count;
    private  String status;
}
