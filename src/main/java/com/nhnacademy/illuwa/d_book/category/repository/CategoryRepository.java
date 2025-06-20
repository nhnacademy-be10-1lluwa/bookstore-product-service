package com.nhnacademy.illuwa.d_book.category.repository;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryNameAndParentCategory(String categoryName, Category parentCategory);
}
