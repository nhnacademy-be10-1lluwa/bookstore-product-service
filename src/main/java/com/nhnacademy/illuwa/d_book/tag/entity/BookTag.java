package com.nhnacademy.illuwa.d_book.tag.entity;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import jakarta.persistence.*;

@Entity
@Table(name = "book_tags")
public class BookTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
