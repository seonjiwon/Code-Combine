package io.github.seonjiwon.code_combine.domain.solution.controller;

import io.github.seonjiwon.code_combine.domain.solution.service.SolutionSyncService;
import io.github.seonjiwon.code_combine.domain.user.entity.User;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SolutionController {

    private final SolutionSyncService solutionSyncService;
    private final UserRepository userRepository;

    @PostMapping("/sync")
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
}
