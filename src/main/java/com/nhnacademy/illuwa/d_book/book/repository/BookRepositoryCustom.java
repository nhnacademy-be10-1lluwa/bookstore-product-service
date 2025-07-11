package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

    Page<Book> findBooksByCriteria(Long categoryId, String tagName, Pageable pageable);

    void deleteBookAndRelatedEntities(Long bookId);
}
