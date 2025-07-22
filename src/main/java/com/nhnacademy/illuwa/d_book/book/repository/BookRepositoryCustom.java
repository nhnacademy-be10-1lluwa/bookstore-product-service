package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepositoryCustom {

//    Page<Book> findBooksByCriteria(Long categoryId, String tagName, Pageable pageable);

    List<Book> findBooksByCategories(List<Long> categoryIds);
}
