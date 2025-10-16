package com.nhnacademy.illuwa.d_book.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "태그 등록 요청 DTO")
public class TagRegisterRequest {

    @NotBlank(message = "태그 이름은 필수입니다.")
    @Schema(description = "태그 이름", example = "소설")
    private String name;
}
