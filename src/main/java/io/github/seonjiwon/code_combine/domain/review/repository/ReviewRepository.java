package io.github.seonjiwon.code_combine.domain.review.repository;

import io.github.seonjiwon.code_combine.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
        "JOIN FETCH r.reviewer " +
        "WHERE r.solution.id = :solutionId " +
        "ORDER BY r.lineNumber")
    List<Review> findAllBySolutionIdWithReviewer(@Param("solutionId") Long solutionId);
}