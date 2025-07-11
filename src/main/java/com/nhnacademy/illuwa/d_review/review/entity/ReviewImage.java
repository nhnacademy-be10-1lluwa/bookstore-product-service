package com.nhnacademy.illuwa.d_review.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review_images")
public class ReviewImage {
    @Id
    @Column(name = "image_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long imageId;

    @Column(length = 1000)
    String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public static ReviewImage of(String imageUrl, Review review) {
        return new ReviewImage(null, imageUrl, review);
    }
}
