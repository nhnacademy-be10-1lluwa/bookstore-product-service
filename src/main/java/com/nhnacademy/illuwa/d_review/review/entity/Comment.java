package com.nhnacademy.illuwa.d_review.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;


    @Column(length = 500, nullable = false)
    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId", nullable = false)
    private Review review;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId", nullable = false)
//    private Members members;
    @Column(nullable = false)
    private Long memberId;
}
