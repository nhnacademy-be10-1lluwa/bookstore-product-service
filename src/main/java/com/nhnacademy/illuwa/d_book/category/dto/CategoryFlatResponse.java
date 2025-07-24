package com.nhnacademy.illuwa.d_book.category.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "평탄화된 카테고리 응답 DTO")
public class CategoryFlatResponse {
    @Schema(description = "카테고리 ID")
    private Long id;
    @Schema(description = "부모 카테고리 ID")
    private Long parentId;
    @Schema(description = "부모 카테고리 이름")
    private String parentName;
    @Schema(description = "카테고리 이름")
    private String categoryName;
    @Schema(description = "카테고리 깊이")
    private int depth;

    public CategoryFlatResponse(Long id, Long parentId, String categoryName) {
        this.id = id;
        this.parentId = parentId;
        this.categoryName = categoryName;
    }
}