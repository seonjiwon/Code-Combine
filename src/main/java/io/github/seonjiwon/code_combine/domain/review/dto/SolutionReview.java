package io.github.seonjiwon.code_combine.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolutionReview {
    private Long reviewId;
    private String reviewerName;
    private String avatarUrl;
    private int lineNumber;
    private String content;
}
