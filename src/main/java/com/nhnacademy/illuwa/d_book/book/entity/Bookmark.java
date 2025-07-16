package com.nhnacademy.illuwa.d_book.book.entity;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Bookmarks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "member_id"})
})
public class Bookmark {
    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static Bookmark of(Book book, Long memberId) {
        return new Bookmark(null, book, memberId);
    }
}
