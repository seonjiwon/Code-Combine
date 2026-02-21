package io.github.seonjiwon.code_combine.domain.review.service;

import io.github.seonjiwon.code_combine.domain.review.domain.Review;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewRequest;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewResponse;
import io.github.seonjiwon.code_combine.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> getReview(Long solutionId) {
        return reviewRepository.findAllBySolutionId(solutionId)
                               .stream()
                               .map(ReviewResponse::convertToReviewResponse)
                               .toList();
    }
}
