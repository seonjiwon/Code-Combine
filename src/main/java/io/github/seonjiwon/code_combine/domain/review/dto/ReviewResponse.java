package io.github.seonjiwon.code_combine.domain.review.dto;

import lombok.Builder;

@Builder
public record ReviewResponse(
    Long reviewId,
    String reviewerName,
    String avatarUrl,
    int lineNumber,
    String content
) {

    public static ReviewResponse convertToReviewResponse(SolutionReview solutionReview) {
        return ReviewResponse.builder()
                             .reviewId(solutionReview.getReviewId())
                             .reviewerName(solutionReview.getReviewerName())
                             .avatarUrl(solutionReview.getAvatarUrl())
                             .lineNumber(solutionReview.getLineNumber())
                             .content(solutionReview.getContent())
                             .build();
    }
}
