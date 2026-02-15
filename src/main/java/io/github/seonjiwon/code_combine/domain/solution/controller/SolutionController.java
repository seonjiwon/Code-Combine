package io.github.seonjiwon.code_combine.domain.solution.controller;

import io.github.seonjiwon.code_combine.domain.solution.dto.DashboardResponse;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionResponse;
import io.github.seonjiwon.code_combine.domain.solution.service.command.SolutionSyncService;
import io.github.seonjiwon.code_combine.domain.solution.service.query.CommitQueryService;
import io.github.seonjiwon.code_combine.domain.solution.service.query.SolutionQueryService;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.service.UserService;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionSyncService solutionSyncService;
    private final SolutionQueryService solutionQueryService;
    private final CommitQueryService commitQueryService;
    private final UserService userService;

    /**
     * 오늘의 커밋 동기화
     */
    @GetMapping("/sync")
    public CustomResponse<String> syncTodayCommits() {
        User user = userService.findUserById(1L);
        String owner = "seonjiwon";
        String repo = "Java-Algorithm";

        log.info("오늘의 커밋 동기화 시작: userId={}, owner={}, repo={}", user.getId(), owner, repo);
        solutionSyncService.syncTodaySolutions(user, owner, repo);

        return CustomResponse.onSuccess("동기화 성공!");
    }

    /**
     * 주간 커밋 통계 조회
     */
    @GetMapping("/dashboard")
    public CustomResponse<DashboardResponse.WeeklyCommitInfo> getWeeklyCommitInfo() {
        DashboardResponse.WeeklyCommitInfo weeklyCommitInfo = commitQueryService.getWeeklyCommitInfo();
        return CustomResponse.onSuccess(weeklyCommitInfo);
    }

    /**
     * 특정 문제의 풀이 상세 조회
     */
    @GetMapping("/{problemId}/solve")
    public CustomResponse<SolutionResponse.Detail> getSolutionDetail(
        @PathVariable Long problemId
    ) {
        SolutionResponse.Detail detailSolution = solutionQueryService.getDetailSolution(problemId);
        return CustomResponse.onSuccess(detailSolution);
    }
}
