package com.nhnacademy.illuwa.domain.review;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId", nullable = false)
    private Reviews reviews;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId", nullable = false)
//    private Members members;
    @Column(nullable = false)
    private Long memberId;
}
