package com.nhnacademy.illuwa.d_book.book.service.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookLike;
import com.nhnacademy.illuwa.d_book.book.repository.BookLikeRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLikeServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLikeRepository bookLikeRepository;

    @Mock
    private BookResponseMapper bookResponseMapper;

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

    @Test
    @DisplayName("좋아요 한 도서 가져오기 (페이징) - 성공")
    void getLikedBooksByMember_Success() {
        // Given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Book book = Book.builder().id(1L).title("Test Book").build();
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book), pageable, 1);
        SimpleBookResponse simpleBookResponse = new SimpleBookResponse();
        simpleBookResponse.setId(1L);
        simpleBookResponse.setTitle("Test Book");
        when(bookLikeRepository.getLikedBooksByMember(memberId, pageable)).thenReturn(bookPage);
        Page<SimpleBookResponse> simpleBookResponsePage = new PageImpl<>(Collections.singletonList(simpleBookResponse), pageable, 1);
        when(bookResponseMapper.toSimpleBookPageResponse(any(Page.class))).thenReturn(simpleBookResponsePage);

        // When
        Page<SimpleBookResponse> result = bookLikeService.getLikedBooksByMember(memberId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Book");
        verify(bookLikeRepository, times(1)).getLikedBooksByMember(memberId, pageable);
    }
}
