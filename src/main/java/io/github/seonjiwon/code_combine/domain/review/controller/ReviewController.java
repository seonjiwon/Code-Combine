package io.github.seonjiwon.code_combine.domain.review.controller;

import io.github.seonjiwon.code_combine.domain.review.dto.ReviewRequest;
import io.github.seonjiwon.code_combine.domain.review.service.ReviewCommandService;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCommandService reviewCommandService;

    @PostMapping("/reviews")
    public CustomResponse<?> createReview(
        @AuthenticationPrincipal Long userId,
        @RequestBody ReviewRequest request
    ) {
        reviewCommandService.createReview(userId, request);
        return CustomResponse.onSuccess("리뷰를 등록했습니다.");
    }
}
