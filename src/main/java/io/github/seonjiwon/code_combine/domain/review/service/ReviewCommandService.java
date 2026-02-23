package io.github.seonjiwon.code_combine.domain.review.service;

import io.github.seonjiwon.code_combine.domain.review.domain.Review;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewRequest;
import io.github.seonjiwon.code_combine.domain.review.repository.ReviewRepository;
import io.github.seonjiwon.code_combine.domain.solution.code.SolutionErrorCode;
import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public void createReview(Long userId, Long solutionId, ReviewRequest request) {
        log.info("Reviewer: {} 등록 시작", userId);
        Solution solution = solutionRepository.findById(solutionId)
            .orElseThrow(() -> new CustomException(SolutionErrorCode.SOLUTION_NOT_FOUND));

        User reviewer = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

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
