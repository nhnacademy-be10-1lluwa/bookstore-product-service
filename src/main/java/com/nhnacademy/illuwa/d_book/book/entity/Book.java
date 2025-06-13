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

    @Column(length = 255, nullable = false)
    private String title;

    private String contents;

    private String description;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    private LocalDate publishedDate;

    @Column(length = 17)
    private String isbn;

    private int regularPrice;

    private int salePrice;

    private boolean isGiftWrap;

    private String imgUrl;

    private String category;
}