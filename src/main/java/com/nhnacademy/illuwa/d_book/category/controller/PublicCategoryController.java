package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Public Category API", description = "공개 카테고리 관련 API")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "메뉴용 카테고리 조회", description = "메뉴 표시를 위한 모든 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 카테고리 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/public/categories")
    public List<CategoryResponse> getCategoriesForMenu() {
        return categoryService.getAllCategories();
    }

}
