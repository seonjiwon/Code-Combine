package io.github.seonjiwon.code_combine.domain.review.controller;

import io.github.seonjiwon.code_combine.domain.review.dto.ReviewRequest;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewResponse;
import io.github.seonjiwon.code_combine.domain.review.service.ReviewCommandService;
import io.github.seonjiwon.code_combine.domain.review.service.ReviewQueryService;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping("/reviews")
    public CustomResponse<String> createReview(
        @AuthenticationPrincipal Long userId,
        @RequestBody ReviewRequest request
    ) {
        reviewCommandService.createReview(userId, request);
        return CustomResponse.onSuccess("리뷰를 등록했습니다.");
    }

    @GetMapping("/reviews/{solutionId}")
    public CustomResponse<List<ReviewResponse>> getReview(
        @PathVariable Long solutionId
    ) {
        List<ReviewResponse> review = reviewQueryService.getReview(solutionId);
        return CustomResponse.onSuccess(review);
    }
}
