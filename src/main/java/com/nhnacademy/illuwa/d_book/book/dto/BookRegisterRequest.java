package com.nhnacademy.illuwa.d_book.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRegisterRequest {

    @NotBlank
    private String title;
    private String contents;
    private String description;
    @NotBlank
    private String author;
    @NotBlank
    private String publisher;
    private LocalDate pubDate;
    @NotBlank
    private String isbn;
    private int regularPrice;
    private int salePrice;
    @NotBlank
    @JsonProperty("cover")
    private String imgUrl;
}
