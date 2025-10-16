package com.nhnacademy.illuwa.d_book.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 검색 요청 DTO")
public class BookSearchRequest {
    @NotBlank
    @Schema(description = "검색할 도서 제목", example = "자바")
    private String title;
}
