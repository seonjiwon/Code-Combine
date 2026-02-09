package io.github.seonjiwon.code_combine.domain.solution.controller;

import io.github.seonjiwon.code_combine.domain.solution.dto.WeeklyCommitInfo;
import io.github.seonjiwon.code_combine.domain.solution.service.command.SolutionSyncService;
import io.github.seonjiwon.code_combine.domain.solution.service.query.SolutionCommitQueryService;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
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
    private final UserRepository userRepository;
    private final SolutionCommitQueryService solutionCommitQueryService;

    @GetMapping("/sync")
    public CustomResponse<String> codeSync() {
        User user = userRepository.findById(1L)
                                  .orElseThrow(
                                      () -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 2. 하드코딩된 값으로 동기화 실행
        String owner = "seonjiwon";
        String repo = "Java-Algorithm";

        log.info("동기화 시작: owner={}, repo={}", owner, repo);
        solutionSyncService.syncTodaySolutions(user, owner, repo);

        return CustomResponse.onSuccess("동기화 성공!");
    }

    @GetMapping("/dashboard")
    public CustomResponse<WeeklyCommitInfo> getWeeklyCommitList() {
        WeeklyCommitInfo weeklyCommitInfo = solutionCommitQueryService.getWeeklyCommitInfo();
        return CustomResponse.onSuccess(weeklyCommitInfo);
    }
}
