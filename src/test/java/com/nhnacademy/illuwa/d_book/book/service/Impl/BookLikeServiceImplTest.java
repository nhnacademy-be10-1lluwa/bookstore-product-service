package com.nhnacademy.illuwa.d_book.book.service.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookLikeRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLikeServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    @InjectMocks
    private BookLikeServiceImpl bookLikeService;

    @Test
    @DisplayName("내가 좋아요 누른 책인지 확인")
    void isLikedByMe() {
        when(bookLikeRepository.existsByBook_IdAndMemberId(anyLong(), anyLong())).thenReturn(true);

        bookLikeService.isLikedByMe(1L, 1L);

        verify(bookLikeRepository).existsByBook_IdAndMemberId(1L, 1L);
    }

    @Test
    @DisplayName("책 좋아요 토글 - 좋아요 추가")
    void toggleBookLikes_addLike() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
        when(bookLikeRepository.existsByBook_IdAndMemberId(anyLong(), anyLong())).thenReturn(false);

        bookLikeService.toggleBookLikes(1L, 1L);

        verify(bookLikeRepository).save(any());
    }

    @Test
    @DisplayName("책 좋아요 토글 - 좋아요 취소")
    void toggleBookLikes_removeLike() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
        when(bookLikeRepository.existsByBook_IdAndMemberId(anyLong(), anyLong())).thenReturn(true);

        bookLikeService.toggleBookLikes(1L, 1L);

        verify(bookLikeRepository).deleteByBook_IdAndMemberId(1L, 1L);
    }
}
