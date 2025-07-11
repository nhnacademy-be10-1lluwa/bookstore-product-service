package com.nhnacademy.illuwa.d_review.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotEmpty
    @Length(min=1, max=50)
    private String reviewTitle;

    @NotEmpty
    @Length(min=1, max=5000)
    private String reviewContent;

    @NotNull
    @Min(1) @Max(5)
    private int reviewRating;
}
