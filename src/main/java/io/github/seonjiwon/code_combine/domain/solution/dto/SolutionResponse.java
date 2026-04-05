package io.github.seonjiwon.code_combine.domain.solution.dto;

import io.github.seonjiwon.code_combine.domain.problem.entity.ProblemTier;
import java.util.List;
import lombok.Builder;

public class SolutionResponse {

    @Builder
    public record Detail(
        String problemName,
        ProblemTier tier,
        String problemUrl,
        List<Submission> submissions
    ) {

    }

    @Builder
    public record Submission(
        Long solutionId,
        String username,
        String language,
        String submissionCode,
        String solveExplain
    ) {

    }
}
