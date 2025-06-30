package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import jakarta.ws.rs.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        List<CategoryResponse> allCategories = categoryService.getAllCategories();
        return ResponseEntity.ok(allCategories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryInfo(@PathVariable Long categoryId) {
        CategoryResponse categoryInfo = categoryService.getCategoryInfo(categoryId);
        return ResponseEntity.ok(categoryInfo);
    }
}