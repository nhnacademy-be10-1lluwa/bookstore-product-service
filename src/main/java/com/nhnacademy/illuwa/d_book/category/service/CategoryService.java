package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryService {

    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = categories.stream().map(c -> new CategoryResponse(
                        c.getId(),
                        c.getParentCategory() != null ? c.getParentCategory().getId() : null,
                        c.getCategoryName()
                ))
                .toList();
        return categoryResponseList;
    }

    public CategoryResponse getCategoryInfo(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));

        return new CategoryResponse(category.getId(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getCategoryName());
    }

}
