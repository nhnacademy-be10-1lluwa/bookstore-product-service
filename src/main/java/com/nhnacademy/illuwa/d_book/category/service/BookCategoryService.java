package com.nhnacademy.illuwa.d_book.category.service;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.stereotype.Service;

@Service
public interface BookCategoryService {
    void deleteByBookId(Long bookId);

    void saveBookCategory(Book book, Category category);
}
