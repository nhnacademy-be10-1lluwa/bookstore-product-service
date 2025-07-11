package com.nhnacademy.illuwa.d_review.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotEmpty
    @Length(min=1, max=50)
    private String reviewTitle;

    @NotEmpty
    @Length(min=1, max=5000)
    private String reviewContent;

    @Min(1) @Max(5)
    private int reviewRating;

    private List<MultipartFile> images;

    private List<String> keepImageUrls;
}
