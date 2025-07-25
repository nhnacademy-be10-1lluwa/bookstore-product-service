package com.nhnacademy.illuwa.d_book.category.service.impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {
    private final BookCategoryRepository bookCategoryRepository;

    @Override
    @Transactional
    public void deleteByBookId(Long bookId) {
        bookCategoryRepository.deleteByBookId(bookId);
    }

    @Override
    public void saveBookCategory(Book book, Category category) {
        BookCategory bookCategory = new BookCategory(book, category);
        bookCategoryRepository.save(bookCategory);
    }
}
