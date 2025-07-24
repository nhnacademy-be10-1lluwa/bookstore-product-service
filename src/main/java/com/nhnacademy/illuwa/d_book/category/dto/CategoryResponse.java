package com.nhnacademy.illuwa.d_book.category.dto;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponse {
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;
    @Schema(description = "부모 카테고리 ID", example = "null")
    private Long parentId;
    @Schema(description = "카테고리 이름", example = "소설")
    private String categoryName;
    @Schema(description = "하위 카테고리 목록")
    private List<CategoryResponse> children = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.parentId = (category.getParentCategory() != null) ? category.getParentCategory().getId() : null;
        this.categoryName = category.getCategoryName();
        this.children = category.getChildrenCategory().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .categoryName(category.getCategoryName())
                .build();
    }
}
