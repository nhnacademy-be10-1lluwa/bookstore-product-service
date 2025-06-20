package com.nhnacademy.illuwa.d_book.category.repository.cateogory;

import com.nhnacademy.illuwa.d_book.category.entity.Category;

import java.util.Optional;

public interface CustomizedCategoryRepository {
    Optional<Category> findByCategoryNameAndParentCategory(String categoryName, Category parentCategory);
}
