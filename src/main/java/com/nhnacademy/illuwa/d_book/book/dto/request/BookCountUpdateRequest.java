package com.nhnacademy.illuwa.d_book.book.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "도서 수량 업데이트 요청 DTO")
public class BookCountUpdateRequest {
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;
    @Schema(description = "도서 수량", example = "10")
    private Integer bookCount;
}
