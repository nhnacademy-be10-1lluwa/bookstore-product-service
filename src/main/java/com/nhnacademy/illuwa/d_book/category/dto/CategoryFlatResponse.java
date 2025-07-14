package com.nhnacademy.illuwa.d_book.category.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryFlatResponse {
    private Long id;
    private Long parentId;
    private String parentName;
    private String categoryName;
    private int depth;

    public CategoryFlatResponse(Long id, Long parentId, String categoryName) {
        this.id = id;
        this.parentId = parentId;
        this.categoryName = categoryName;
    }
}