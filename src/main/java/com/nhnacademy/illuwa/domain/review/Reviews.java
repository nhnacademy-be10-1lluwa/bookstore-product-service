package com.nhnacademy.illuwa.domain.review;

import com.nhnacademy.illuwa.domain.book.Books;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String reviewTitle;

    @Column(nullable = false)
    private String reviewContent;

    //파일 경로
    private String reviewImage;

    @Column(nullable = false)
    private Integer reviewScore;

    @Column(nullable = false)
    private LocalDateTime reviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", nullable = false)
    private Books books;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId", nullable = false)
//    private Members members;
    @Column(nullable = false)
    private Long memberId;
}
