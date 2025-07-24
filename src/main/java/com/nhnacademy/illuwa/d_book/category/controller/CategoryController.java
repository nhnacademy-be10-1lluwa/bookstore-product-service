package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Tag(name = "카테고리 API", description = "카테고리 관리 및 조회 관련 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카테고리 정보 조회", description = "카테고리 ID로 특정 카테고리의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryInfo(@Parameter(description = "조회할 카테고리 ID", required = true) @PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryInfo(categoryId));
    }

    @Operation(summary = "모든 카테고리 조회 (트리 또는 페이징)", description = "모든 카테고리를 트리 구조 또는 페이징된 목록으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(oneOf = {CategoryResponse.class, Page.class})))
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @Parameter(description = "조회 방식 (tree: 트리 구조, 기본: 일반 목록)", example = "tree") @RequestParam(required = false) String view,
            Pageable pageable
    ) {

        if ("tree".equalsIgnoreCase(view)) {
            return ResponseEntity.ok(categoryService.getCategoryTree());
        }

        if (pageable != null && pageable.isPaged()) {
            Page<CategoryResponse> paged = categoryService.getAllCategoriesByPaging(pageable);
            return ResponseEntity.ok(paged.getContent());
        }
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "평면 카테고리 페이징 조회", description = "모든 카테고리를 평면 구조로 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CategoryFlatResponse.class)))
    })
    @GetMapping("/flat")
    public ResponseEntity<Page<CategoryFlatResponse>> getFlatCategoriesPaged(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategoriesFlatPaged(pageable));
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 ID를 통해 카테고리를 삭제합니다. 하위 카테고리가 있는 경우 삭제할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "하위 카테고리 존재",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "삭제할 카테고리 ID", required = true) @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
