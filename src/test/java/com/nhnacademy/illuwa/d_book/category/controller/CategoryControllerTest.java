package com.nhnacademy.illuwa.d_book.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {


    @Autowired
    MockMvc mockMvc;
    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory_Success() throws Exception {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest(null, "새 카테고리");

        // When & Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(categoryService, times(1)).createCategory(any(CategoryCreateRequest.class));
    }

    @Test
    @DisplayName("카테고리 정보 조회 - 성공")
    void getCategoryInfo_Success() throws Exception {
        // Given
        Long categoryId = 1L;
        CategoryResponse mockResponse = new CategoryResponse(categoryId, null, "테스트 카테고리", null);
        given(categoryService.getCategoryInfo(categoryId)).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리"));

        verify(categoryService, times(1)).getCategoryInfo(categoryId);
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 일반")
    void getAllCategories_Normal() throws Exception {
        // Given
        Page<CategoryResponse> mockPage = new PageImpl<>(List.of(new CategoryResponse(1L, null, "카테고리1", null)));
        given(categoryService.getAllCategoriesByPaging(any(Pageable.class))).willReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("카테고리1"));

        verify(categoryService, times(1)).getAllCategoriesByPaging(any(Pageable.class));
        verify(categoryService, never()).getAllCategories();
        verify(categoryService, never()).getCategoryTree();
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 트리 뷰")
    void getAllCategories_Tree() throws Exception {
        // Given
        List<CategoryResponse> mockTree = List.of(new CategoryResponse(1L, null, "부모", List.of(new CategoryResponse(2L, 1L, "자식", null))));
        given(categoryService.getCategoryTree()).willReturn(mockTree);

        // When & Then
        mockMvc.perform(get("/api/categories")
                        .param("view", "tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("부모"))
                .andExpect(jsonPath("$[0].children[0].categoryName").value("자식"));

        verify(categoryService, times(1)).getCategoryTree();
        verify(categoryService, never()).getAllCategories();
        verify(categoryService, never()).getAllCategoriesByPaging(any(Pageable.class));
    }

    @Test
    @DisplayName("모든 카테고리 조회 - 페이징")
    void getAllCategories_Paged() throws Exception {
        // Given
        Page<CategoryResponse> mockPage = new PageImpl<>(List.of(new CategoryResponse(1L, null, "페이징 카테고리", null)));
        given(categoryService.getAllCategoriesByPaging(any(Pageable.class))).willReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("페이징 카테고리"));

        verify(categoryService, times(1)).getAllCategoriesByPaging(any(Pageable.class));
        verify(categoryService, never()).getAllCategories();
        verify(categoryService, never()).getCategoryTree();
    }

    @Test
    @DisplayName("평탄화된 카테고리 목록 조회 - 성공")
    void getFlatCategoriesPaged_Success() throws Exception {
        // Given
        Page<CategoryFlatResponse> mockPage = new PageImpl<>(List.of(new CategoryFlatResponse(1L, null, "부모", "카테고리1", 1)));
        given(categoryService.getAllCategoriesFlatPaged(any(Pageable.class))).willReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/categories/flat")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryName").value("카테고리1"));

        verify(categoryService, times(1)).getAllCategoriesFlatPaged(any(Pageable.class));
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_Success() throws Exception {
        // Given
        Long categoryId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}
