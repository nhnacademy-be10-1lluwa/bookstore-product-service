package com.nhnacademy.illuwa.d_review.comment;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentEntityUnitTest {
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
            new BookExtraInfo(Status.OUT_OF_STOCK,false,1)
    );

    private final Review review = new Review(
            3L,
            "리뷰임",
            "내용없음",
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

        // when
        comment = Comment.of(commentContents, commentDate, review, memberId);
    }

    @Test
    @DisplayName("Comment Entity of 메서드 테스트")
    void entityOfMethodTest(){
        // then
        assertThat(comment.getCommentContents()).isEqualTo("댓글댓글댓글");
        assertThat(comment.getCommentDate()).isEqualTo(LocalDateTime.of(2025, 6, 20, 17, 40));
        assertThat(comment.getReview().getReviewId()).isEqualTo(3L);
        assertThat(comment.getMemberId()).isEqualTo(7777L);
    }

    @Test
    @DisplayName("Comment Entity update 메서드 테스트")
    void entityUpdateMethodTest() {
        // when
        Comment test = comment;
        String newContents = "ㄹㅇㅋㅋ";
        test.update(newContents);

        // then
        assertThat(test.getCommentContents()).isEqualTo("ㄹㅇㅋㅋ");
    }

    @Test
    @DisplayName("Comment Entity update 메서드 테스트 - Null or Blank")
    void entityUpdateWithNullTest() {
        // when
        Comment test = comment;
        test.update(null);

        // then
        assertThat(test.getCommentContents()).isEqualTo("댓글댓글댓글");

        // when 2
        test.update(" ");

        // then 2
        assertThat(test.getCommentContents()).isEqualTo("댓글댓글댓글");
    }
}
