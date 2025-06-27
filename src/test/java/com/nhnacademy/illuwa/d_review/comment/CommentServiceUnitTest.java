package com.nhnacademy.illuwa.d_review.comment;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentStatusInvalidException;
import com.nhnacademy.illuwa.d_review.comment.repository.CommentRepository;
import com.nhnacademy.illuwa.d_review.comment.service.CommentService;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CommentRepository commentRepository;

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

    @Test
    @DisplayName("댓글 작성")
    void createCommentTest() {
        // given
        Long memberId = 7777L;
        CommentRequest request = new CommentRequest("메롱메롱");
        LocalDateTime commentDate = LocalDateTime.of(2025, 1, 1, 20, 30, 0);

        Comment comment = Comment.of(
                request.getCommentContents(),
                commentDate,
                review,
                memberId
        );

        // mock
        Mockito.when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        // when
        CommentResponse response = commentService.createComment(review.getReviewId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCommentContents()).isEqualTo("메롱메롱");
        assertThat(response.getCommentDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 20, 30, 0));
        assertThat(response.getReviewId()).isEqualTo(3L);
        assertThat(response.getMemberId()).isEqualTo(7777L);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰의 댓글 작성")
    void createCommentWithInvalidReviewTest(){
        // given
        CommentRequest request = new CommentRequest("메롱메롱");

        // mock
        Mockito.when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(review.getReviewId(), request))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("리뷰를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 목록 가져오기")
    void getCommentListTest() {
        // given
        List<LocalDateTime> commentDates = new ArrayList<>();
        List<Comment> savedComments = new ArrayList<>();

        for(int i=0; i<5; i++){
            commentDates.add(LocalDateTime.of(2025, 1, i+1, 20, 30, 15));
            Comment comment = Comment.of(
                    "댓글 " + (i+1),
                    commentDates.get(i),
                    review,
                    (long) i+1
            );
            savedComments.add(comment);
        }

        // mock
        Mockito.when(commentRepository.findByReview_ReviewId(review.getReviewId())).thenReturn(savedComments);

        // when
        List<CommentResponse> commentResponseList = commentService.getCommentList(review.getReviewId());

        // then
        assertThat(commentResponseList).isNotNull();
        assertThat(commentResponseList.size()).isEqualTo(5);
        for(int i=0; i<5; i++){
            CommentResponse commentResponse = commentResponseList.get(i);

            assertThat(commentResponse.getCommentId()).isEqualTo(savedComments.get(i).getCommentId());
            assertThat(commentResponse.getCommentContents()).isEqualTo("댓글 " + (i+1));
            assertThat(commentResponse.getCommentDate()).isEqualTo(LocalDateTime.of(2025, 1, i+1, 20, 30, 15));
            assertThat(commentResponse.getReviewId()).isEqualTo(3L);
            assertThat(commentResponse.getMemberId()).isEqualTo((long) i+1);
        }
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() {
        // given
        Long memberId = 7777L;
        LocalDateTime commentDate = LocalDateTime.of(2025, 1, 1, 20, 30, 0);

        Comment comment = new Comment(
                2L,
                "대충 나쁜 말",
                commentDate,
                review,
                memberId
        );

        CommentRequest request = new CommentRequest("대충 좋은 말");

        // mock
        Mockito.when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        Mockito.when(commentRepository.findById(comment.getCommentId())).thenReturn(Optional.of(comment));

        // when
        CommentResponse response = commentService.updateComment(review.getReviewId(), comment.getCommentId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCommentContents()).isEqualTo("대충 좋은 말");
    }

    @Test
    @DisplayName("댓글 수정 - 존재하지 않는 댓글의 수정")
    void updateCommentIsNotExistTest() {
        // given
        CommentRequest request = new CommentRequest("대충 좋은 말");
        Long commentId = 9999L;

        // mock
        Mockito.when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(review.getReviewId(), commentId, request))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 수정 - 리뷰 ID와 댓글 ID 불일치")
    void updateCommentWithInvalidIdTest() {
        // given
        Review anotherReview = new Review(
                99L,
                "리뷰임",
                "내용없음",
                5,
                LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                book,
                1111L
        );

        Long memberId = 7777L;
        LocalDateTime commentDate = LocalDateTime.of(2025, 1, 1, 20, 30, 0);

        Comment comment = new Comment(
                2L,
                "대충 나쁜 말",
                commentDate,
                anotherReview,
                memberId
        );

        CommentRequest request = new CommentRequest("대충 좋은 말");

        // mock
        Mockito.when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        Mockito.when(commentRepository.findById(comment.getCommentId())).thenReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(review.getReviewId(), comment.getCommentId(), request))
                .isInstanceOf(CommentStatusInvalidException.class)
                .hasMessageContaining("리뷰와 댓글이 일치하지 않습니다.");
    }
}
