package com.nhnacademy.illuwa.d_review.comment;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.comment.repository.CommentRepository;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class CommentRepositoryJpaTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Review review;

    @BeforeEach
    void setUp() {
        Book book = new Book(
                null,
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
                new BookExtraInfo(Status.OUT_OF_STOCK, false)
        );
        testEntityManager.persist(book);

        review = new Review(
                null,
                "리뷰임",
                "내용없음",
                null,
                5,
                LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                book,
                1111L
        );
        testEntityManager.persist(review);
    }

    @Test
    @DisplayName("댓글 목록 가져오기 테스트")
    void findByReview_ReviewIdTest(){
        // given
        List<LocalDateTime> commentDates = new ArrayList<>();
        List<Comment> savedComments = new ArrayList<>();

        // given
        for(int i=0; i<5; i++){
            commentDates.add(LocalDateTime.of(2025, 1, i+1, 20, 30, 15));
            Comment comment = Comment.of(
                    "댓글 " + (i+1),
                    commentDates.get(i),
                    review,
                    (long) i+1
            );
            testEntityManager.persist(comment);
            savedComments.add(comment);
        }
        testEntityManager.flush();
        testEntityManager.clear();

        // when
        List<Comment> comments = commentRepository.findByReview_ReviewId(review.getReviewId());

        assertThat(comments.size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            Comment comment = comments.get(i);
            assertThat(comment.getCommentId()).isEqualTo(savedComments.get(i).getCommentId());
            assertThat(comment.getCommentContents()).isEqualTo("댓글 "+ (i+1));
            assertThat(comment.getCommentDate()).isEqualTo(LocalDateTime.of(2025, 1, 1+i, 20, 30, 15));
            assertThat(comment.getReview().getReviewId()).isEqualTo(review.getReviewId());
            assertThat(comment.getMemberId()).isEqualTo((long) i+1);
        }
    }
}
