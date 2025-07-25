package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.exception.CategoryAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.category.exception.CategoryNotAllowedException;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리 생성 - 성공 (부모 카테고리 없음)")
    void createCategory_Success_NoParent() {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest(null, "새 카테고리");
        Category newCategory = new Category("새 카테고리");
        newCategory.setId(1L);

        given(categoryRepository.findByCategoryName(request.getCategoryName())).willReturn(Optional.empty());
        given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

        // When
        categoryService.createCategory(request);

        // Then
        verify(categoryRepository, times(1)).findByCategoryName(request.getCategoryName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 성공 (부모 카테고리 있음)")
    void createCategory_Success_WithParent() {
        // Given
        Category parentCategory = new Category("부모 카테고리");
        parentCategory.setId(1L);
        CategoryCreateRequest request = new CategoryCreateRequest(1L, "자식 카테고리");
        Category newCategory = new Category("자식 카테고리");
        newCategory.setId(2L);
        newCategory.setParentCategory(parentCategory);

        given(categoryRepository.findByCategoryName(request.getCategoryName())).willReturn(Optional.empty());
        given(categoryRepository.findById(request.getParentId())).willReturn(Optional.of(parentCategory));
        given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

        // When
        categoryService.createCategory(request);

        // Then
        verify(categoryRepository, times(1)).findByCategoryName(request.getCategoryName());
        verify(categoryRepository, times(1)).findById(request.getParentId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 실패 (이미 존재하는 카테고리 이름)")
    void createCategory_Failure_AlreadyExists() {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest(null, "기존 카테고리");
        given(categoryRepository.findByCategoryName(request.getCategoryName())).willReturn(Optional.of(new Category("기존 카테고리")));

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("이미 존재하는 카테고리입니다.");

        verify(categoryRepository, times(1)).findByCategoryName(request.getCategoryName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 실패 (부모 카테고리 없음)")
    void createCategory_Failure_ParentNotFound() {
        // Given
        CategoryCreateRequest request = new CategoryCreateRequest(99L, "새 카테고리");
        given(categoryRepository.findByCategoryName(request.getCategoryName())).willReturn(Optional.empty());
        given(categoryRepository.findById(request.getParentId())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryNotAllowedException.class)
                .hasMessageContaining("상위 카테고리가 존재하지 않습니다.");

        verify(categoryRepository, times(1)).findByCategoryName(request.getCategoryName());
        verify(categoryRepository, times(1)).findById(request.getParentId());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getAllCategories() {

        // given
        Category parent = new Category("부모카테고리");
        parent.setId(1L);

        Category child = new Category("자식카테고리");
        child.setId(2L);
        child.setParentCategory(parent);

        List<Category> mockCategoryList = List.of(parent, child);

        given(categoryRepository.findAll()).willReturn(mockCategoryList);

        // when
        List<CategoryResponse> result = categoryService.getAllCategories();

        // then
        assertEquals(2, result.size());

        CategoryResponse parentResponse = result.get(0);
        assertEquals(1L, parentResponse.getId());
        assertEquals("부모카테고리", parentResponse.getCategoryName());
        assertNull(parentResponse.getParentId());

        CategoryResponse childResponse = result.get(1);
        assertEquals(2L, childResponse.getId());
        assertEquals("자식카테고리", childResponse.getCategoryName());
        assertEquals(1L, childResponse.getParentId());
    }

    @Test
    @DisplayName("카테고리 정보 조회 - 성공")
    void getCategoryInfo_Success() {
        // Given
        Long categoryId = 1L;
        Category category = new Category("테스트 카테고리");
        category.setId(categoryId);

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // When
        CategoryResponse result = categoryService.getCategoryInfo(categoryId);

        // Then
        assertEquals(categoryId, result.getId());
        assertEquals("테스트 카테고리", result.getCategoryName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("카테고리 정보 조회 - 실패 (카테고리 없음)")
    void getCategoryInfo_NotFound() {
        // Given
        Long categoryId = 1L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryInfo(categoryId))
                .isInstanceOf(CategoryNotAllowedException.class)
                .hasMessageContaining("존재하지 않는 카테고리입니다.");

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("모든 카테고리 페이징 조회 - 성공")
    void getAllCategoriesByPaging_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Category category = new Category("테스트 카테고리");
        category.setId(1L);
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category), pageable, 1);

        given(categoryRepository.findAll(pageable)).willReturn(categoryPage);

        // When
        Page<CategoryResponse> result = categoryService.getAllCategoriesByPaging(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("테스트 카테고리", result.getContent().get(0).getCategoryName());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("모든 카테고리 평탄화 페이징 조회 - 성공")
    void getAllCategoriesFlatPaged_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Category category = new Category("테스트 카테고리");
        category.setId(1L);
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category), pageable, 1);

        given(categoryRepository.findAll(pageable)).willReturn(categoryPage);

        // When
        Page<CategoryFlatResponse> result = categoryService.getAllCategoriesFlatPaged(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("테스트 카테고리", result.getContent().get(0).getCategoryName());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_Success() {
        // Given
        Long categoryId = 1L;
        Category category = new Category("테스트 카테고리");
        category.setId(categoryId);

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(bookCategoryRepository.findByCategoryId(categoryId)).willReturn(Collections.emptyList());

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("카테고리 삭제 - 실패 (카테고리 없음)")
    void deleteCategory_NotFound() {
        // Given
        Long categoryId = 1L;

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotAllowedException.class)
                .hasMessageContaining("존재하지 않는 카테고리");

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}