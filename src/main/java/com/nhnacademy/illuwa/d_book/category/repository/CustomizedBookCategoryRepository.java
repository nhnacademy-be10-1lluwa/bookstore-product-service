package com.nhnacademy.illuwa.d_book.category.repository;

import java.util.List;

public interface CustomizedBookCategoryRepository
{
    List<String> findCategoryNamesByBookId(Long bookId);
}
