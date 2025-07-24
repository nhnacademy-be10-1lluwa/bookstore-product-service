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
@Schema(description = "카테고리 생성 요청 DTO")
public class CategoryCreateRequest {
    @Schema(description = "카테고리 이름", example = "소설")
    private String categoryName;
    @Schema(description = "부모 카테고리 ID (최상위 카테고리인 경우 null)", example = "1")
    private Long parentId;
}

