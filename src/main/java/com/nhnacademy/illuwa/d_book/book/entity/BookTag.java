package com.nhnacademy.illuwa.d_book.book.entity;

import com.nhnacademy.illuwa.d_book.tag.Tag;
import jakarta.persistence.*;

@Entity
public class BookTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
