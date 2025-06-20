package com.nhnacademy.illuwa.d_book.category.repository.cateogory;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> , CustomizedCategoryRepository{
}
