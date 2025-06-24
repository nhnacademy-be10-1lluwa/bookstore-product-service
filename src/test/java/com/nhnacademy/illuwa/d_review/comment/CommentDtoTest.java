package com.nhnacademy.illuwa.d_review.comment;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDtoTest {
    private final Book book = new Book(
            10L,
            "이상한 책",
            "목차",
            "아무 설명",
            "이상한 사람",
            "무명출판",
            LocalDate.of(1970, 1, 1),
            "11111111111111111",
            99999999,
            99999999,
            null,
            new BookExtraInfo(Status.OUT_OF_STOCK,false)
    );

    private final Review review = new Review(
            3L,
            "리뷰임",
            "내용없음",
            null,
            5,
            LocalDateTime.of(2024, 12, 31, 23, 59, 59),
            book,
            1111L
    );

    private Comment comment;

    @BeforeEach
    void setUp() {
        // given
        String commentContents = "댓글댓글댓글";
        LocalDateTime commentDate = LocalDateTime.of(2025, 6, 20, 17, 40);

        Long memberId = 7777L;

        comment = Comment.of(commentContents, commentDate, review, memberId);
    }

    @Test
    @DisplayName("Comment From 메서드 테스트")
    void responseFromMethodTest() {
        // when
        CommentResponse response = CommentResponse.from(comment);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCommentContents()).isEqualTo("댓글댓글댓글");
        assertThat(response.getCommentDate()).isEqualTo(LocalDateTime.of(2025, 6, 20, 17, 40));
        assertThat(response.getReviewId()).isEqualTo(3L);
        assertThat(response.getMemberId()).isEqualTo(7777L);
    }
}
