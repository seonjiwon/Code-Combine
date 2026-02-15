package io.github.seonjiwon.code_combine.domain.problem.dto;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.dto.UserSolver;
import java.util.List;
import lombok.Builder;

public class ProblemsResponse {
    @Builder
    public record ProblemSolveList(
        List<SolveInfo> problemList,
        String cursor
    ) {

        public static ProblemSolveList convertToProblemSolveList(List<SolveInfo> problemList, String cursor) {
            return ProblemSolveList.builder()
                .problemList(problemList)
                .cursor(cursor)
                .build();
        }
    }

    @Builder
    public record SolveInfo(
        Long problemId,
        int problemNumber,
        String problemName,
        int solvedUserCount,
        List<SolvedUser> solvedUsers
    ) {

        public static SolveInfo convertToSolveInfo(Problem problem, List<UserSolver> solvers) {
            return SolveInfo.builder()
                .problemId(problem.getId())
                .problemNumber(problem.getProblemNumber())
                .problemName(problem.getTitle())
                .solvedUserCount(solvers.size())
                .build();
        }
    }

    @Builder
    public record SolvedUser(
        Long userId,
        String username,
        String avatarUrl
    ) {

        public static SolvedUser convertToSolvedUser(UserSolver solver) {
            return SolvedUser.builder()
                .userId(solver.getUserId())
                .username(solver.getUsername())
                .avatarUrl(solver.getAvatarUrl())
                .build();
        }
    }
}
