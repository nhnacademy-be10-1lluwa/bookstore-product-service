package com.nhnacademy.illuwa.d_book.category.repository.bookcategory;

import java.util.List;

public interface CustomizedBookCategoryRepository {
    List<String> findCategoryNamesByBookId(Long bookId);

    long deleteByBookId(Long bookId);
}
