package com.nhnacademy.illuwa.d_book.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String contents;

    private String description;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate publishedDate;

    @Column(length = 17, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private int regularPrice;

    @Column(nullable = false)
    private int salePrice;

    private boolean giftWrap;

    @Column(nullable = false)
    private String imgUrl;

}