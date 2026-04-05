package io.github.seonjiwon.code_combine.domain.problem.dto;


import io.github.seonjiwon.code_combine.domain.problem.entity.Problem;
import io.github.seonjiwon.code_combine.domain.problem.entity.ProblemTier;
import io.github.seonjiwon.code_combine.domain.solution.entity.Solution;
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
        ProblemTier tier,
        int solvedUserCount,
        List<SolvedUser> solvedUsers
    ) {

        public static SolveInfo from(Problem problem, List<Solution> solutions) {
            List<SolvedUser> solvedUsers = solutions.stream()
                                                    .map(SolvedUser::from)
                                                    .toList();

            return SolveInfo.builder()
                            .problemId(problem.getId())
                            .problemNumber(problem.getProblemNumber())
                            .problemName(problem.getTitle())
                            .tier(problem.getTier())
                            .solvedUserCount(solvedUsers.size())
                            .solvedUsers(solvedUsers)
                            .build();
        }
    }

    @Builder
    public record SolvedUser(
        String username,
        String avatarUrl
    ) {

        public static SolvedUser from(Solution solution) {
            return SolvedUser.builder()
                             .username(solution.getUser().getUsername())
                             .avatarUrl(solution.getUser().getAvatarUrl())
                             .build();
        }
    }
}