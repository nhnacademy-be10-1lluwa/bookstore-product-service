package com.nhnacademy.illuwa.d_book.category.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "카테고리 생성 요청 DTO")
public class CategoryCreateRequest {

    @Schema(description = "부모 카테고리 ID", example = "1", nullable = true)
    private Long parentId;

    @Schema(description = "카테고리 이름", example = "소설")
    private String categoryName;
}
