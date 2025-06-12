package com.nhnacademy.illuwa.d_review.review.entity;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.nhnacademy.illuwa.d_book.book.entity.Book;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(length = 50, nullable = false)
    private String reviewTitle;

    @Column(length = 5000, nullable = false)
    private String reviewContent;

    //파일 경로
    private String reviewImageUrl;

    @Column(nullable = false)
    private Integer reviewRating;

    @Column(nullable = false)
    private LocalDateTime reviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId", nullable = false)
//    private Members members;
    @Column(nullable = false)
    private Long memberId;

    public static Review of(String title, String content, String imageUrl, Integer rating, LocalDateTime date, Book book, Long memberId) {
        return new Review(null, title, content, imageUrl, rating, date, book, memberId);
    }

    public void update(String title, String content, String imageUrl, Integer rating) {
        if (title != null) { this.reviewTitle = title; }
        if (content != null) { this.reviewContent = content; }
        if (imageUrl != null && !imageUrl.isBlank()) { this.reviewImageUrl = imageUrl; }
        if (rating != null) { this.reviewRating = rating; }
        this.reviewDate = LocalDateTime.now();
    }

}
