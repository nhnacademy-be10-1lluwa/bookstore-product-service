package com.nhnacademy.illuwa.d_book.category.dto;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .build();
    }


}