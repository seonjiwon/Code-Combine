package io.github.seonjiwon.code_combine.domain.solution.repository;

import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    Boolean existsByCommitSha(String commitSha);
}
