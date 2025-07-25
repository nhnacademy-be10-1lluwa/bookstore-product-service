package com.nhnacademy.illuwa.d_book.category.controller;

import com.nhnacademy.illuwa.d_book.category.dto.CategoryCreateRequest;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryFlatResponse;
import com.nhnacademy.illuwa.d_book.category.dto.CategoryResponse;
import com.nhnacademy.illuwa.d_book.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "카테고리 관련 API")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "카테고리가 성공적으로 생성되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카테고리 정보 조회", description = "카테고리 ID를 통해 특정 카테고리의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 카테고리 정보를 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryInfo(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryInfo(categoryId));
    }

    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리 목록을 조회합니다. 'tree' 뷰 또는 페이징된 목록으로 조회할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 카테고리 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(required = false) String view,
            Pageable pageable
    ) {

        if ("tree".equalsIgnoreCase(view)) {
            return ResponseEntity.ok(categoryService.getCategoryTree());
        }

        CacheControl cacheControl = CacheControl.maxAge(10, TimeUnit.MINUTES);
        // 페이징 요청 처리
        if (pageable != null && pageable.isPaged()) {
            Page<CategoryResponse> paged = categoryService.getAllCategoriesByPaging(pageable);
            return ResponseEntity.ok(paged.getContent());
        }
        // 기본 전체 조회
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "평탄화된 카테고리 목록 조회 (페이징)", description = "모든 카테고리를 평탄화된 형태로 페이징하여 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 평탄화된 카테고리 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CategoryFlatResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/flat")
    public ResponseEntity<Page<CategoryFlatResponse>> getFlatCategoriesPaged(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategoriesFlatPaged(pageable));
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 ID를 통해 카테고리를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "카테고리가 성공적으로 삭제되었습니다."),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}