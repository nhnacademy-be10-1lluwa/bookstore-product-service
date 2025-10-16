package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.service.impl.BookImageServiceImpl;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookImageServiceTest {

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private MinioStorageService minioStorageService;

    @InjectMocks
    private BookImageServiceImpl bookImageService;

    @Test
    @DisplayName("책 ID로 이미지 삭제")
    void deleteByBookId() {
        bookImageService.deleteByBookId(1L);

        verify(bookImageRepository).deleteAllByBook_Id(anyLong());
    }

    @Test
    @DisplayName("도서 이미지 저장 - 성공")
    void saveBookImage_Success() throws IOException {
        // Given
        Book mockBook = mock(Book.class);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        String imageUrl = "http://example.com/test.jpg";

        when(minioStorageService.uploadBookImage(any(MockMultipartFile.class))).thenReturn(imageUrl);
        bookImageService.saveBookImage(mockBook, mockFile, ImageType.THUMBNAIL);

        // Then
        verify(bookImageRepository, times(1)).save(any(BookImage.class));
    }
}
