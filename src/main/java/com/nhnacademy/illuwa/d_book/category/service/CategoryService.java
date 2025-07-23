package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.exception.CategoryNotAllowedException;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

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

        List<BookCategory> bookCategories = bookCategoryRepository.findByCategoryId(categoryId);

        if (!bookCategories.isEmpty()) {
            bookCategoryRepository.deleteAll(bookCategories);
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategoriesByPaging(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryResponse::fromEntity);
    }
    private void flattenCategory(Category category, int depth, List<CategoryFlatResponse> flatList) {
        flatList.add(new CategoryFlatResponse(
                category.getId(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getParentCategory() != null ? category.getParentCategory().getCategoryName() : null,
                category.getCategoryName(),
                depth
        ));
        for (Category child : category.getChildrenCategory()) {
            flattenCategory(child, depth + 1, flatList);
        }
    }

    @Transactional(readOnly = true)
    public Page<CategoryFlatResponse> getAllCategoriesFlatPaged(Pageable pageable) {
        // 1) 모든 카테고리 가져오기
        List<Category> allCategories = categoryRepository.findAll();

        // 2) 계층 평면화
        List<CategoryFlatResponse> flatList = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getParentCategory() == null) {
                flattenCategory(category, 0, flatList);
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), flatList.size());
        List<CategoryFlatResponse> pageContent = flatList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, flatList.size());
    }




}
