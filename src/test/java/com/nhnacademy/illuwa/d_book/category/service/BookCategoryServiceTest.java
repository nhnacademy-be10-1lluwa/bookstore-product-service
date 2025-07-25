package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.service.impl.BookCategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCategoryServiceTest {

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private BookCategoryServiceImpl bookCategoryService;

    @Test
    @DisplayName("책 ID로 카테고리 삭제")
    void deleteByBookId() {
        bookCategoryService.deleteByBookId(1L);

        verify(bookCategoryRepository).deleteByBookId(anyLong());
    }

    @Test
    @DisplayName("도서 카테고리 저장 - 성공")
    void saveBookCategory_Success() {
        // Given
        Book mockBook = mock(Book.class);
        Category mockCategory = mock(Category.class);

        // When
        bookCategoryService.saveBookCategory(mockBook, mockCategory);

        // Then
        verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
    }
}
