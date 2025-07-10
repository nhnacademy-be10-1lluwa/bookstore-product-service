package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.exception.CategoryNotAllowedException;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = categories.stream().map(CategoryResponse::new)
                .toList();
        return categoryResponseList;
    }


    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }


    public CategoryResponse getCategoryInfo(Long categoryId) {
        Optional<Category> categoryById = categoryRepository.findById(categoryId);

        if(categoryById.isEmpty()){
            throw new CategoryNotAllowedException("존재하지 않는 카테고리입니다..");
        }

        Category category = categoryById.get();

        return new CategoryResponse(category);
    }

    public Page<CategoryResponse> getAllCategoriesByPaging(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        Page<CategoryResponse> categoryPageMap = categoryPage.map(category ->
                new CategoryResponse(category)
        );

        return categoryPageMap;
    }

    public CategoryResponse registerCategory(CategoryCreateRequest categoryCreateRequest){
        Optional<Category> categoryById = categoryRepository.findById(categoryCreateRequest.getParentId());

        if(categoryById.isEmpty()){
            throw new CategoryNotAllowedException("허용되지 않는 카테고리 입니다.");
        }

        Category parentCategory = categoryById.get();

        Category newCategory = new Category(categoryCreateRequest.getCategoryName());

        parentCategory.addChildCategory(newCategory);

        return new CategoryResponse(newCategory);
    }

    @Transactional
    public void createCategory(CategoryCreateRequest request) {
        Category newCategory = new Category(request.getCategoryName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리가 존재하지 않습니다."));
            parent.addChildCategory(newCategory);
        }

        categoryRepository.save(newCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리"));

        if (!category.getChildrenCategory().isEmpty()) {
            throw new IllegalStateException("하위 카테고리를 포함한 경우 삭제 불가");
        }

        categoryRepository.delete(category);
    }


}
