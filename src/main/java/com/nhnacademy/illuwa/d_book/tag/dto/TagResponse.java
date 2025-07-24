package com.nhnacademy.illuwa.d_book.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "태그 응답 DTO")
public class TagResponse {
    @Schema(description = "태그 ID", example = "1")
    private Long id;
    @Schema(description = "태그 이름", example = "소설")
    private String name;
}

