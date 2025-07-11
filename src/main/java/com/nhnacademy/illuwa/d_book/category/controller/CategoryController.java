package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CategoryResponse>> getAllCategoriesByPaging(Pageable pageable){
        return ResponseEntity.ok(categoryService.getAllCategoriesByPaging(pageable));
    }

    @GetMapping()
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryInfo(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryInfo(categoryId));
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/tree")
    public List<CategoryResponse> getCategoryTree() {
        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
        return categoryTree;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }




}