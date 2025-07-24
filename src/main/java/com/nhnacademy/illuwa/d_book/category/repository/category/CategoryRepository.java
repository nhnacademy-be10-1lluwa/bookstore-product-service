package com.nhnacademy.illuwa.d_book.category.repository.category;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> , CustomizedCategoryRepository{
    @EntityGraph(attributePaths = {"childrenCategory", "parentCategory"})
    List<Category> findByParentCategoryIsNull();

    @EntityGraph(attributePaths = {"childrenCategory", "parentCategory"})
    Optional<Category> findByCategoryName(String categoryName);

    @EntityGraph(attributePaths = {"childrenCategory", "parentCategory"})
    List<Category> findAll();

    @EntityGraph(attributePaths = {"childrenCategory", "parentCategory"})
    Page<Category> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"childrenCategory", "parentCategory"})
    Optional<Category> findById(Long id);
}
