package io.github.seonjiwon.code_combine.domain.solution.repository;

import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    Boolean existsByCommitSha(String commitSha);

    List<Solution> findBySolvedAtGreaterThanEqualAndSolvedAtLessThan(LocalDateTime start,
                                                  LocalDateTime end);

    List<Solution> findByProblemIdIn(List<Long> problemIds);
}
