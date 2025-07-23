package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryInfo(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryInfo(categoryId));
    }


//    @GetMapping("/tree")
//    public List<CategoryResponse> getCategoryTree() {
//        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
//        return categoryTree;
//    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(required = false) String view,
            Pageable pageable
    ) {

        if ("tree".equalsIgnoreCase(view)) {
            return ResponseEntity.ok(categoryService.getCategoryTree());
        }

        // 페이징 요청 처리
        if (pageable != null && pageable.isPaged()) {
            Page<CategoryResponse> paged = categoryService.getAllCategoriesByPaging(pageable);
            return ResponseEntity.ok(paged.getContent());
        }
        // 기본 전체 조회
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/flat")
    public ResponseEntity<Page<CategoryFlatResponse>> getFlatCategoriesPaged(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategoriesFlatPaged(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}