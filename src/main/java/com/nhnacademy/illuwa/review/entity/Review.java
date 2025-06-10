package com.nhnacademy.illuwa.review.entity;

import com.nhnacademy.illuwa.book.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String reviewImage;

    @Column(nullable = false)
    private Integer reviewScore;

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
}
