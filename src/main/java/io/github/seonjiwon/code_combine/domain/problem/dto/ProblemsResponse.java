package io.github.seonjiwon.code_combine.domain.problem.dto;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import java.util.List;
import lombok.Builder;

public class ProblemsResponse {

    @Builder
    public record ProblemSolveList(
        List<SolveInfo> problemList,
        String cursor
    ) {

        public static ProblemSolveList from(List<SolveInfo> problemList, String cursor) {
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

        public static SolveInfo from(Problem problem, List<UserSolverProjection> solvers) {
            return SolveInfo.builder()
                            .problemId(problem.getId())
                            .problemNumber(problem.getProblemNumber())
                            .problemName(problem.getTitle())
                            .solvedUserCount(solvers.size())
                            .solvedUsers(solvers.stream()
                                                .map(SolvedUser::from)
                                                .toList())

                            .build();
        }
    }

    @Builder
    public record SolvedUser(
        String username,
        String avatarUrl
    ) {

        public static SolvedUser from(UserSolverProjection solver) {
            return SolvedUser.builder()
                             .username(solver.username())
                             .avatarUrl(solver.avatarUrl())
                             .build();
        }
    }
}
