package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/categories")
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

}