package io.github.seonjiwon.code_combine.domain.problem.repository;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByProblemNumber(int problemNumber);
}
