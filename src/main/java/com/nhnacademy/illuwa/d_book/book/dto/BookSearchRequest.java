package com.nhnacademy.illuwa.d_book.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchRequest {
    @NotBlank
    private String title;
}
