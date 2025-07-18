package com.nhnacademy.illuwa.d_review.comment.entity;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 500, nullable = false)
    private String commentContents;

    @Column(nullable = false)
    private LocalDateTime commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public static Comment of(String commentContents, LocalDateTime commentDate, Review review, Long memberId) {
        return new Comment(
                null,
                commentContents,
                commentDate,
                review,
                memberId
        );
    }

    public void update(String content) {
        if(content != null && !content.isBlank()) { this.commentContents = content; }
        this.commentDate = LocalDateTime.now();
    }
}
