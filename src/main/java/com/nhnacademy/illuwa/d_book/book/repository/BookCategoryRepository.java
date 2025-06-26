package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookCategoryRepository extends JpaRepository<BookCategory,Long> {

}