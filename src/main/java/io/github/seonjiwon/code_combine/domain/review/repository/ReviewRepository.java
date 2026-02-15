package io.github.seonjiwon.code_combine.domain.review.repository;

import io.github.seonjiwon.code_combine.domain.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
