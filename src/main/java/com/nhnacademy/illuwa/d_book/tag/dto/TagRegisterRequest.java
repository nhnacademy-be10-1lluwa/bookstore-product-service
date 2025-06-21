package com.nhnacademy.illuwa.d_book.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TagRegisterRequest {

    @NotBlank(message = "태그 이름은 필수입니다.")

    private String name;
}
