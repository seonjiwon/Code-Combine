package io.github.seonjiwon.code_combine.domain.problem.service;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.ProblemSolveList;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.SolveInfo;
import io.github.seonjiwon.code_combine.domain.problem.dto.UserSolverProjection;
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
        log.info("문제 목록 조회 시작");

        // 1. 문제 조회 (+1개 더 조회하여 hasNext 판단)
        List<Problem> problems = fetchProblems(cursor, PAGE_SIZE + 1);

        // 2. 페이지네이션 처리
        boolean hasNext = problems.size() > PAGE_SIZE;
        if (hasNext) {
            problems.remove(problems.size() - 1);
        }
        String nextCursor = generateNextCursor(problems, hasNext);

        // 3. 문제별 풀이자 조회
        Map<Long, List<UserSolverProjection>> solverMap = groupSolversByProblem(problems);

        // 4. 응답 생성
        List<SolveInfo> solveInfos = buildSolveInfoList(problems, solverMap);

        log.info("문제 목록 조회 완료");

        return ProblemSolveList.from(solveInfos, nextCursor);
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
        return String.valueOf(problems.get(problems.size() - 1).getId());
    }

    /**
     * 문제별 풀이자 조회 및 그룹핑
     */
    private Map<Long, List<UserSolverProjection>> groupSolversByProblem(List<Problem> problems) {
        if (problems.isEmpty()) {
            return Map.of();
        }

        // 1. problemId 가져오기
        List<Long> problemIds = problems.stream()
                                        .map(Problem::getId)
                                        .toList();

        // 2. 문제를 푼 사용자 전부 조회 (N+1 방지를 위해 한번에)
        log.info("문제를 푼 사용자 조회: problemIds 수={}", problemIds.size());
        List<UserSolverProjection> solvers = problemRepository.findSolversByProblemIds(problemIds);
        log.info("조회된 풀이자 수: {}", solvers.size());

        // 3. 문제 번호 별로 그룹핑
        return solvers.stream()
                      .collect(Collectors.groupingBy(UserSolverProjection::problemId));
    }

    /**
     * SolveInfo 리스트 생성
     */
    private List<SolveInfo> buildSolveInfoList(List<Problem> problems,
                                               Map<Long, List<UserSolverProjection>> solverMap) {
        return problems.stream()
                       .map(problem -> SolveInfo.from(
                           problem,
                           solverMap.get(problem.getId())
                       ))
                       .toList();
    }
}
