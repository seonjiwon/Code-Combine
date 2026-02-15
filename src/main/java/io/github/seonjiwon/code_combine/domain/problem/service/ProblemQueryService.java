package io.github.seonjiwon.code_combine.domain.problem.service;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.ProblemSolveList;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.SolveInfo;
import io.github.seonjiwon.code_combine.domain.problem.dto.UserSolver;
import io.github.seonjiwon.code_combine.domain.problem.repository.ProblemRepository;
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

/**
 * Problem 도메인의 Query(Read) 처리 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemQueryService {

    private final ProblemRepository problemRepository;
    private static final int PAGE_SIZE = 10;

    /**
     * 문제 목록 조회 (커서 기반 페이지네이션)
     */
    public ProblemSolveList getProblemList(String cursor) {
        log.info("문제 목록 조회 시작 - cursor: {}", cursor);

        // 1. 문제 조회 (+1개 더 조회하여 hasNext 판단)
        List<Problem> problems = fetchProblems(cursor, PAGE_SIZE + 1);

        // 2. 페이지네이션 처리
        boolean hasNext = problems.size() > PAGE_SIZE;
        List<Problem> pagedProblems = hasNext ? problems.subList(0, PAGE_SIZE) : problems;
        String nextCursor = generateNextCursor(problems, hasNext);

        // 3. 문제별 풀이자 조회 (N+1 문제 방지)
        Map<Long, List<UserSolver>> solverMap = fetchSolversGroupedByProblem(pagedProblems);

        // 4. 응답 생성
        List<SolveInfo> solveInfos = buildSolveInfoList(pagedProblems, solverMap);

        log.info("문제 목록 조회 완료 - 조회된 문제 수: {}, hasNext: {}",
            solveInfos.size(), nextCursor != null);

        return ProblemSolveList.builder()
            .problemList(solveInfos)
            .cursor(nextCursor)
            .build();
    }

    /**
     * 커서 기반으로 문제 조회
     */
    private List<Problem> fetchProblems(String cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("problemNumber"));

        if (cursor == null || cursor.isEmpty()) {
            return problemRepository.findProblems(pageable);
        }
        return problemRepository.findProblems(Long.parseLong(cursor), pageable);
    }

    /**
     * 다음 커서 생성
     */
    private String generateNextCursor(List<Problem> problems, boolean hasNext) {
        if (!hasNext || problems.isEmpty()) {
            return null;
        }
        return String.valueOf(problems.get(PAGE_SIZE).getId());
    }

    /**
     * 문제별 풀이자 조회 및 그룹핑 (N+1 방지)
     */
    private Map<Long, List<UserSolver>> fetchSolversGroupedByProblem(List<Problem> problems) {
        if (problems.isEmpty()) {
            return Map.of();
        }

        List<Long> problemIds = problems.stream()
            .map(Problem::getId)
            .toList();

        log.debug("문제를 푼 사용자 조회: problemIds 수={}", problemIds.size());
        List<UserSolver> solvers = problemRepository.findSolversByProblemIds(problemIds);
        log.debug("조회된 풀이자 수: {}", solvers.size());

        return solvers.stream()
            .collect(Collectors.groupingBy(UserSolver::getProblemId));
    }

    /**
     * SolveInfo 리스트 생성
     */
    private List<SolveInfo> buildSolveInfoList(List<Problem> problems,
                                                Map<Long, List<UserSolver>> solverMap) {
        return problems.stream()
            .map(problem -> buildSolveInfo(
                problem,
                solverMap.getOrDefault(problem.getId(), List.of())
            ))
            .toList();
    }

    /**
     * SolveInfo 생성
     */
    private SolveInfo buildSolveInfo(Problem problem, List<UserSolver> solvers) {
        return SolveInfo.builder()
            .problemId(problem.getId())
            .problemNumber(problem.getProblemNumber())
            .problemName(problem.getTitle())
            .solvedUserCount(solvers.size())
            .build();
    }
}
