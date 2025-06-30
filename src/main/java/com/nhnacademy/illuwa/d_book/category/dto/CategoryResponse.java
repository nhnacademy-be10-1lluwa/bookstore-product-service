package com.nhnacademy.illuwa.d_book.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private Long parentCategoryId;
    private String CategoryName;
}
