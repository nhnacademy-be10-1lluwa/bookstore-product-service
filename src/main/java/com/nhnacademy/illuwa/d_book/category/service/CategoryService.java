package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.exception.CategoryNotAllowedException;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Category> categoryById = categoryRepository.findById(categoryId);

        if(categoryById.isEmpty()){
            throw new CategoryNotAllowedException("존재하지 않는 카테고리입니다..");
        }

        Category category = categoryById.get();

        return new CategoryResponse(category.getId(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getCategoryName());
    }

    public Page<CategoryResponse> getAllCategoriesByPaging(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        Page<CategoryResponse> categoryPageMap = categoryPage.map(category ->
                new CategoryResponse(category.getId(), category.getParentCategory().getId(), category.getCategoryName())
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

}
