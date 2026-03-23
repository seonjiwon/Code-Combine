package io.github.seonjiwon.code_combine.domain.review.service;

import io.github.seonjiwon.code_combine.domain.review.dto.ReviewResponse;
import io.github.seonjiwon.code_combine.domain.review.entity.Review;
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

    /**
     * 특정 풀이의 리뷰 목록 조회
     */
    public List<ReviewResponse> getReview(Long solutionId) {
        List<Review> reviews = reviewRepository.findAllBySolutionIdWithReviewer(solutionId);

        return reviews.stream()
                      .map(review -> new ReviewResponse(
                          review.getId(),
                          review.getReviewer().getUsername(),
                          review.getReviewer().getAvatarUrl(),
                          review.getLineNumber(),
                          review.getContent()
                      ))
                      .toList();
    }
}