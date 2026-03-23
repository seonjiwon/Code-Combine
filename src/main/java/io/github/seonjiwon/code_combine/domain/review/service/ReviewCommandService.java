package io.github.seonjiwon.code_combine.domain.review.service;

import io.github.seonjiwon.code_combine.domain.review.entity.Review;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewRequest;
import io.github.seonjiwon.code_combine.domain.review.repository.ReviewRepository;
import io.github.seonjiwon.code_combine.domain.solution.entity.Solution;
import io.github.seonjiwon.code_combine.domain.solution.service.query.SolutionQueryService;
import io.github.seonjiwon.code_combine.domain.user.entity.User;
import io.github.seonjiwon.code_combine.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;

    private final SolutionQueryService solutionQueryService;
    private final UserQueryService userQueryService;

    public void createReview(Long userId, Long solutionId, ReviewRequest request) {
        log.info("Reviewer: {} 등록 시작", userId);

        Solution solution = solutionQueryService.getById(solutionId);
        User reviewer = userQueryService.getById(userId);

        Review review = Review.builder()
            .solution(solution)
            .reviewer(reviewer)
            .lineNumber(request.lineNumber())
            .content(request.content())
            .build();

        reviewRepository.save(review);
        log.info("Reviewer: {} 등록 완료", userId);
    }
}
