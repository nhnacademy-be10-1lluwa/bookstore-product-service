package com.nhnacademy.illuwa.d_book.book.entity;

import com.nhnacademy.illuwa.d_book.book.enums.orderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String bookName;

    private String contents;

    private String description;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    private Date publishedDateTime;

    @Column(length = 17)
    private String isbn;

    private int regularPrice;

    private int salePrice;

    private boolean isGiftWrap;

    private orderStatus status;
}