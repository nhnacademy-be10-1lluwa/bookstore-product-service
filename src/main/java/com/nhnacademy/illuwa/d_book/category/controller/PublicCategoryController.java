package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "페이지 헤더 섹션 카테고리 API", description = "메뉴 등에 사용되는 공개 카테고리 조회 API")
@RestController
@AllArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "모든 카테고리 조회", description = "메뉴 등에 사용될 모든 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    })
    @GetMapping("/api/public/categories")
    public List<CategoryResponse> getCategoriesForMenu() {
        return categoryService.getAllCategories();
    }

}
