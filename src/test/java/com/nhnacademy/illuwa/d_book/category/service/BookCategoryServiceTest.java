package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookCategoryServiceTest {

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private BookCategoryService bookCategoryService;

    @Test
    @DisplayName("책 ID로 카테고리 삭제")
    void deleteByBookId() {
        bookCategoryService.deleteByBookId(1L);

        verify(bookCategoryRepository).deleteByBookId(anyLong());
    }
}
