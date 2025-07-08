package com.nhnacademy.illuwa.d_book.category.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryCreateRequest {

    private Long parentId;

    private String categoryName;
}
