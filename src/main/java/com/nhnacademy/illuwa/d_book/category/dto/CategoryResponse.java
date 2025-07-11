package com.nhnacademy.illuwa.d_book.category.dto;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private Long parentId;
    private String categoryName;
    private List<CategoryResponse> children = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.categoryName = category.getCategoryName();
        if (category.getParentCategory() != null) {
            this.parentId = category.getParentCategory().getId();
        }

        if (category.getChildrenCategory() != null) {
            this.children = category.getChildrenCategory().stream()
                    .map(CategoryResponse::new)
                    .collect(Collectors.toList());
        }
    }
}