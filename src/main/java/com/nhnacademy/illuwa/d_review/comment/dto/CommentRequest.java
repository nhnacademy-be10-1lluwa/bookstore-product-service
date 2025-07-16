package com.nhnacademy.illuwa.d_review.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotEmpty
    @Length(min = 1, max = 500)
    private String commentContents;
}
