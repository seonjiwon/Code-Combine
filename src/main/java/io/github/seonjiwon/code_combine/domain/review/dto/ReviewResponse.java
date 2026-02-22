package io.github.seonjiwon.code_combine.domain.review.dto;

public record ReviewResponse(
    Long reviewId,
    String reviewerName,
    String avatarUrl,
    int lineNumber,
    String content
) {
}
