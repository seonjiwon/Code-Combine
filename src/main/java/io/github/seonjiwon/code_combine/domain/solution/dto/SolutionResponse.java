package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class SolutionResponse {
    @Builder
    public record Detail (
        List<Submission> submissions
    ) {

    }

    @Builder
    public record Submission (
        String username,
        String language,
        String submissionCode,
        String solveExplain
    ) {

    }
}
