package com.nhnacademy.illuwa.d_review.reviewlike.entity;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "member_id"})
})
public class ReviewLike {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static ReviewLike of(Review review, Long memberId) {
        return new ReviewLike(null, review, memberId);
    }
}
