package io.github.seonjiwon.code_combine.domain.review.dto;

import lombok.Builder;

@Builder
public record ReviewRequest(
    Long solutionId,
    int lineNumber,
    String content
) {

}
