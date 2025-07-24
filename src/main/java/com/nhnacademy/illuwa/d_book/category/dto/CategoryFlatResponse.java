package com.nhnacademy.illuwa.d_book.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평면 카테고리 응답 DTO")
public class CategoryFlatResponse {
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;
    @Schema(description = "부모 카테고리 ID", example = "null")
    private Long parentId;
    @Schema(description = "부모 카테고리 이름", example = "null")
    private String parentName;
    @Schema(description = "카테고리 이름", example = "소설")
    private String categoryName;
    @Schema(description = "카테고리 깊이 (0부터 시작)", example = "0")
    private int depth;
}
