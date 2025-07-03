package com.nhnacademy.illuwa.d_book.category.dto;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private Long parentCategoryId;
    private String CategoryName;

    public CategoryResponse(Category category){
        this.id = category.getId();
        this.parentCategoryId = category.getParentCategory().getId();
        this.CategoryName = category.getCategoryName();
    }
}
