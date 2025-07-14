package com.nhnacademy.illuwa.d_book.category.repository.bookcategory;

import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, CustomizedBookCategoryRepository {
    Optional<BookCategory> findByBookId(Long bookId);
    List<BookCategory> findByCategoryId(Long categoryId);
}
