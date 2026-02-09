package io.github.seonjiwon.code_combine.domain.solution.repository;

import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.solution.dto.ProblemSolution;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    Boolean existsByCommitSha(String commitSha);

    List<Solution> findBySolvedAtGreaterThanEqualAndSolvedAtLessThan(LocalDateTime start,
                                                  LocalDateTime end);

    @Query("SELECT new io.github.seonjiwon.code_combine.domain.solution.dto.ProblemSolution(s.sourceCode, s.language, u.username, u.avatarUrl) " +
        "FROM Solution s " +
        "JOIN s.user u " +
        "WHERE s.problem.id = :problemId")
    List<ProblemSolution> findAllSolutionsByProblemId(@Param("problemId") Long problemId);
}
