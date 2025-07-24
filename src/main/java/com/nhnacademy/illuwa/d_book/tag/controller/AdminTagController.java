package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.common.dto.PageResponse;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Tag(name = "관리자 태그 API", description = "관리자를 위한 태그 관리 관련 API")
@RestController
@RequestMapping("/api/admin")
public class AdminTagController {

    private final TagService tagService;


    AdminTagController(TagService tagService){
        this.tagService = tagService;
    }


    @Operation(summary = "태그 목록 조회", description = "모든 태그 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping("/tags")
    public ResponseEntity<PageResponse<TagResponse>> getTags(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<TagResponse> result = tagService.getAllTags(pageable);

        PageResponse<TagResponse> response = new PageResponse<>(
                result.getContent(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.isLast(),
                result.getSize(),
                result.getNumber()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 생성 성공",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/tags")
    public ResponseEntity<TagResponse> createTag(@RequestBody TagRegisterRequest request) {
        TagResponse tag = tagService.registerTag(request.getName());
        return ResponseEntity.ok(tag);
    }



    @Operation(summary = "태그 삭제", description = "태그 ID를 통해 태그를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "태그 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("tags/{id}")
    public ResponseEntity<Void> deleteTag(@Parameter(description = "삭제할 태그 ID", required = true) @PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
