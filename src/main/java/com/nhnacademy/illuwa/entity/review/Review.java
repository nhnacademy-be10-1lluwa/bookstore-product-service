package com.nhnacademy.illuwa.entity.review;

import com.nhnacademy.illuwa.entity.book.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @NotBlank
    @Length(max = 50)
    @Column(length = 50, nullable = false)
    private String reviewTitle;

    @NotBlank
    @Length(max = 5000)
    @Column(length = 5000, nullable = false)
    private String reviewContent;

    //파일 경로
    private String reviewImage;

    @Size(min = 1, max = 5)
    @Column(nullable = false)
    private Integer reviewScore;

    @NotNull
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
