package com.nhnacademy.illuwa.d_book.book.entity;

import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
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

    @Column(length = 13, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private int regularPrice;

    @Column(nullable = false)
    private int salePrice;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    private List<BookImage> bookImages;

    @Embedded
    private BookExtraInfo bookExtraInfo;

    public void addImage(BookImage image) {
        this.bookImages.add(image);
        image.setBook(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookTag> bookTags = new ArrayList<>();

}