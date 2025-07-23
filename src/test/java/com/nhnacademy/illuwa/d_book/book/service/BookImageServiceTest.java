package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookImageServiceTest {

    @Mock
    private BookImageRepository bookImageRepository;

    @InjectMocks
    private BookImageService bookImageService;

    @Test
    @DisplayName("책 ID로 이미지 삭제")
    void deleteByBookId() {
        bookImageService.deleteByBookId(1L);

        verify(bookImageRepository).deleteAllByBook_Id(anyLong());
    }
}
