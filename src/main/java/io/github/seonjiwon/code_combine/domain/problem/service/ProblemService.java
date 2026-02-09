package io.github.seonjiwon.code_combine.domain.problem.service;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemSolveList;
import io.github.seonjiwon.code_combine.domain.problem.dto.SolveInfo;
import io.github.seonjiwon.code_combine.domain.problem.dto.SolvedUser;
import io.github.seonjiwon.code_combine.domain.problem.dto.UserSolver;
import io.github.seonjiwon.code_combine.domain.problem.repository.ProblemRepository;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
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
public class ProblemService {

    private final ProblemRepository problemRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;

    public ProblemSolveList getProblemSolveList(String cursor) {
        // 1. 페이지 설정
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE + 1, Sort.by("problemNumber"));

        // 2. 정보 가져오기
        List<Problem> problems;
        if (cursor == null || cursor.isEmpty()) {
            problems = problemRepository.findProblems(pageable);
        } else {
            Long cursorId = Long.parseLong(cursor);
            problems = problemRepository.findProblems(cursorId, pageable);
        }

        // 3. 초과 데이터 제거
        boolean hasMore = problems.size() > DEFAULT_PAGE_SIZE;
        if (hasMore) {
            problems.remove(problems.size() - 1);
        }

        // 4. 다음 커서 생성
        String nextCursor = hasMore
            ? String.valueOf(problems.get(problems.size() - 1).getId())
            : null;

        // 5. ProblemId 목록 추출
        List<Long> problemIds = problems.stream()
                                        .map(Problem::getId)
                                        .toList();

        // 6. 해당 Problem 을 푼 User 정보 조회
        List<UserSolver> solvers = problemRepository.findSolversByProblemIds(problemIds);

        // 7. ProblemId 별로 User 정보 그룹핑
        Map<Long, List<UserSolver>> solversByProblem = solvers.stream()
                                                              .collect(
                                                                  Collectors.groupingBy(
                                                                      UserSolver::getProblemId
                                                                  ));

        // 8. SolveInfo 변환
        List<SolveInfo> solveInfos = problems.stream()
                                             .map(problem -> {
                                                 List<UserSolver> problemSolvers = solversByProblem.get(
                                                     problem.getId());

                                                 List<SolvedUser> solvedUsers = problemSolvers.stream()
                                                                                              .map(
                                                                                                  problemSolver -> SolvedUser.builder()
                                                                                                                             .userId(problemSolver.getUserId())
                                                                                                                             .username(problemSolver.getUsername())
                                                                                                                             .avatarUrl(problemSolver.getAvatarUrl())
                                                                                                                             .build())
                                                                                              .toList();

                                                 return SolveInfo.builder()
                                                                 .problemId(problem.getId())
                                                                 .problemNumber(problem.getProblemNumber())
                                                                 .problemName(problem.getTitle())
                                                                 .solvedUserCount(solvedUsers.size())
                                                                 .build();
                                             })
                                             .toList();

        return ProblemSolveList.builder()
                               .problemList(solveInfos)
                               .cursor(nextCursor)
                               .build();
    }
}
