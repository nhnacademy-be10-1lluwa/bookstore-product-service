package com.nhnacademy.illuwa.d_review.review.entity;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @Column(name = "review_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(length = 50, nullable = false)
    private String reviewTitle;

    @Column(length = 5000, nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private Integer reviewRating;

    @Column(nullable = false)
    private LocalDateTime reviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static Review of(String title, String content, Integer rating, LocalDateTime date, Book book, Long memberId) {
        return new Review(null, title, content, rating, date, book, memberId);
    }

    public void update(String title, String content, Integer rating) {
        if (title != null) { this.reviewTitle = title; }
        if (content != null) { this.reviewContent = content; }
        if (rating != null) { this.reviewRating = rating; }
        this.reviewDate = LocalDateTime.now();
    }
}
