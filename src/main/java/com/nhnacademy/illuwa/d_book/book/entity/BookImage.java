package com.nhnacademy.illuwa.d_book.book.entity;

import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class BookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    public BookImage(Book book, String imageUrl, ImageType imageType) {
        this.book = book;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
    }

}
