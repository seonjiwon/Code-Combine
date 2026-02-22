package io.github.seonjiwon.code_combine.domain.review.repository;

import io.github.seonjiwon.code_combine.domain.review.domain.Review;
import io.github.seonjiwon.code_combine.domain.review.dto.ReviewResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT new io.github.seonjiwon.code_combine.domain.review.dto.ReviewResponse(" +
        "r.id, u.username, u.avatarUrl, r.lineNumber, r.content) " +
        "FROM Review r " +
        "JOIN r.reviewer u " +
        "WHERE r.solution.id = :solutionId " +
        "ORDER BY r.lineNumber")
    List<ReviewResponse> findAllBySolutionId(Long solutionId);
}
