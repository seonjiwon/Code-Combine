package io.github.seonjiwon.code_combine.domain.problem.dto;

import java.util.List;
import lombok.Builder;

public class ProblemsResponse {
    @Builder
    public record ProblemSolveList(
        List<SolveInfo> problemList,
        String cursor
    ) {

    }

    @Builder
    public record SolveInfo(
        Long problemId,
        int problemNumber,
        String problemName,
        int solvedUserCount,
        List<SolvedUser> solvedUsers
    ) {

    }

    @Builder
    public record SolvedUser(
        Long userId,
        String username,
        String avatarUrl
    ) {

    }
}
