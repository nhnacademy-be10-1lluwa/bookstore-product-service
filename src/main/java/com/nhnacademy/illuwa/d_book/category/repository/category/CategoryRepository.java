package com.nhnacademy.illuwa.d_book.category.repository.category;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> , CustomizedCategoryRepository{
}
