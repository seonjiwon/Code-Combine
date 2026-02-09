package io.github.seonjiwon.code_combine.domain.problem.service.query.v0;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.ProblemSolveList;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.SolveInfo;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.SolvedUser;
import io.github.seonjiwon.code_combine.domain.problem.dto.UserSolver;
import io.github.seonjiwon.code_combine.domain.problem.repository.ProblemRepository;
import io.github.seonjiwon.code_combine.domain.problem.service.query.ProblemQueryService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemQueryServiceV0 implements ProblemQueryService {

    private final ProblemRepository problemRepository;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public ProblemSolveList getProblemSolveList(String cursor) {
        // 1. 데이터 조회
        List<Problem> problems = getProblems(cursor);

        // 2. 페이지네이션 처리
        boolean hasNext = problems.size() > DEFAULT_PAGE_SIZE;
        String nextCursor = generateNextCursor(problems, hasNext);
        List<Problem> pagedProblems = getPagedProblems(problems, hasNext);

        // 3. 문제를 푼 사용자 조회
        Map<Long, List<UserSolver>> solversByProblem = getSolverGroupedByProblem(pagedProblems);

        // 3. SolveInfo 변환
        List<SolveInfo> solveInfos = convertToSolveInfo(problems, solversByProblem);

        return ProblemSolveList.builder()
                               .problemList(solveInfos)
                               .cursor(nextCursor)
                               .build();
    }


    private List<Problem> getProblems(String cursor) {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE + 1, Sort.by("problemNumber"));

        if (cursor != null && !cursor.isEmpty()) {
            return problemRepository.findProblems(Long.parseLong(cursor), pageable);
        }
        return problemRepository.findProblems(pageable);
    }


    private String generateNextCursor(List<Problem> problems, boolean hasNext) {
        if (!hasNext || problems.isEmpty()) {
            return null;
        }
        return String.valueOf(problems.get(problems.size() - 1).getId());
    }

    private List<Problem> getPagedProblems(List<Problem> problems, boolean hasNext) {
        if (hasNext) {
            return problems.subList(0, DEFAULT_PAGE_SIZE);
        }
        return problems;
    }


    private Map<Long, List<UserSolver>> getSolverGroupedByProblem(List<Problem> problems) {
        List<Long> problemIds = problems.stream()
                                        .map(Problem::getId)
                                        .toList();

        List<UserSolver> solvers = problemRepository.findSolversByProblemIds(problemIds);

        return solvers.stream()
                      .collect(Collectors.groupingBy(
                          UserSolver::getProblemId
                      ));
    }

    private static List<SolveInfo> convertToSolveInfo(List<Problem> problems,
                                                      Map<Long, List<UserSolver>> solversByProblem) {
        return problems.stream()
                       .map(problem -> {
                           List<UserSolver> solvers = solversByProblem.get(problem.getId());
                           return convertToSolveInfo(problem, solvers);
                       })
                       .toList();
    }

    private static SolveInfo convertToSolveInfo(Problem problem, List<UserSolver> solvers) {
        return SolveInfo.builder()
                        .problemId(problem.getId())
                        .problemNumber(problem.getProblemNumber())
                        .problemName(problem.getTitle())
                        .solvedUserCount(solvers.size())
                        .build();
    }
}
