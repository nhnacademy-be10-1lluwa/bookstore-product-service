package com.nhnacademy.illuwa.d_book.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 수량 업데이트 요청 DTO")
public class BookCountUpdateRequest {
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;
    @Schema(description = "변경할 도서 수량", example = "5")
    private Integer bookCount;
}
