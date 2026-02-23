package io.github.seonjiwon.code_combine.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ReviewRequest(
    @Positive(message = "라인 번호는 양수여야 합니다.")
    int lineNumber,
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    String content
) {

}
