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

    /**
     * 문제 목록 조회 (커서 기반 페이지네이션)
     * @param cursor
     * @return
     */
    public ProblemSolveList getProblemSolveList(String cursor) {
        log.info("=== 문제 목록 조회 시작 ===");
        // 1. 데이터 조회
        List<Problem> problems = getProblems(cursor);
        log.info("조회된 문제 수: {}", problems.size());

        // 2. 페이지네이션 처리
        boolean hasNext = problems.size() > DEFAULT_PAGE_SIZE;
        String nextCursor = generateNextCursor(problems, hasNext);
        List<Problem> pagedProblems = getPagedProblems(problems, hasNext);

        // 3. 문제를 푼 사용자 조회
        Map<Long, List<UserSolver>> solversByProblem = getSolverGroupedByProblem(pagedProblems);
        log.info("문제를 푼 사용자 정보 조회 완료: {}개 문제", solversByProblem.size());

        // 3. SolveInfo 변환
        List<SolveInfo> solveInfos = convertToSolveInfo(pagedProblems, solversByProblem);
        log.info("=== 문제 목록 조회 완료 ===");

        return ProblemSolveList.builder()
                               .problemList(solveInfos)
                               .cursor(nextCursor)
                               .build();
    }


    /**
     * 커서 기반 문제 조회
     * @param cursor
     * @return
     */
    private List<Problem> getProblems(String cursor) {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE + 1, Sort.by("problemNumber"));

        if (cursor != null && !cursor.isEmpty()) {
            return problemRepository.findProblems(Long.parseLong(cursor), pageable);
        }
        return problemRepository.findProblems(pageable);
    }


    /**
     * 다음 커서 생성
     * hasNext 가 true 면 마지막 문제의 ID 커서로 반환
     */
    private String generateNextCursor(List<Problem> problems, boolean hasNext) {
        if (!hasNext || problems.isEmpty()) {
            return null;
        }
        return String.valueOf(problems.get(problems.size() - 1).getId());
    }

    /**
     * 패이지네이션 처리
     * hasNext 가 true 면 마지막 요소 제거
     */
    private List<Problem> getPagedProblems(List<Problem> problems, boolean hasNext) {
        if (hasNext) {
            return problems.subList(0, DEFAULT_PAGE_SIZE);
        }
        return problems;
    }

    /**
     * 문제별로 푼 사용자 조회 및 그룹핑
     * N+1 문제 방지를 위해 단일 쿼리로 조회 후 Stream 으로 그룹핑
     */
    private Map<Long, List<UserSolver>> getSolverGroupedByProblem(List<Problem> problems) {
        List<Long> problemIds = problems.stream()
                                        .map(Problem::getId)
                                        .toList();

        log.info("문제를 푼 사용자 조회: problemIds={}", problemIds);
        List<UserSolver> solvers = problemRepository.findSolversByProblemIds(problemIds);
        log.info("조회된 사용자 수: {}", solvers.size());

        return solvers.stream()
                      .collect(Collectors.groupingBy(
                          UserSolver::getProblemId
                      ));
    }

    /**
     * Problem 리스트를 SolveInfo 로 변환
     */
    private static List<SolveInfo> convertToSolveInfo(List<Problem> problems,
                                                      Map<Long, List<UserSolver>> solversByProblem) {
        return problems.stream()
                       .map(problem -> {
                           List<UserSolver> solvers = solversByProblem.get(problem.getId());
                           return convertToSolveInfo(problem, solvers);
                       })
                       .toList();
    }

    /**
     * Problem 엔티티를 SolveInfo Dto 로 변환
     */
    private static SolveInfo convertToSolveInfo(Problem problem, List<UserSolver> solvers) {
        return SolveInfo.builder()
                        .problemId(problem.getId())
                        .problemNumber(problem.getProblemNumber())
                        .problemName(problem.getTitle())
                        .solvedUserCount(solvers.size())
                        .build();
    }
}
