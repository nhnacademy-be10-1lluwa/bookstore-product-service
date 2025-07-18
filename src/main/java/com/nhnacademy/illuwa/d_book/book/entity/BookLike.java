package com.nhnacademy.illuwa.d_book.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "member_id"})
})
public class BookLike {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static BookLike of(Book book, Long memberId) {
        return new BookLike(null, book, memberId);
    }
}
