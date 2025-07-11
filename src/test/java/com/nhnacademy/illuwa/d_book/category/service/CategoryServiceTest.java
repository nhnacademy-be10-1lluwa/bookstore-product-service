package com.nhnacademy.illuwa.d_book.category.service;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

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
}