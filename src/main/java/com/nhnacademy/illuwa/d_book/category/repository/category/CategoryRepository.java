package com.nhnacademy.illuwa.d_book.category.repository.category;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> , CustomizedCategoryRepository{
    List<Category> findByParentCategoryIsNull();

    @Override
    List<Category> findAll();

    Optional<Category> findByCategoryName(String categoryName);
}
