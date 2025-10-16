package com.nhnacademy.illuwa.d_book.tag.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "태그 응답 DTO")
public class TagResponse {
    @Schema(description = "태그 ID")
    private Long id;
    @Schema(description = "태그 이름")
    private String name;
}

