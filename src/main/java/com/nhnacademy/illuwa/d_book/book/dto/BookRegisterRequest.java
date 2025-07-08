package com.nhnacademy.illuwa.d_book.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRegisterRequest {

    private String title;
    private String author;
    private String publisher;
    private String contents;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String pubDate;
    private String isbn;
    private int regularPrice;
    private int salePrice;
    private String description;
    private MultipartFile imageFile;
    private Integer count;
    private Long categoryId;


    public LocalDate getParsedPubDate() {
        return LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
