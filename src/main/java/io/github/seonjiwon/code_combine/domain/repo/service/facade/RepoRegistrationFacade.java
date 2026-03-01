package io.github.seonjiwon.code_combine.domain.repo.service.facade;

import io.github.seonjiwon.code_combine.domain.repo.dto.RepoRegisterRequest;
import io.github.seonjiwon.code_combine.domain.repo.dto.RepoRegistrationResult;
import io.github.seonjiwon.code_combine.domain.repo.service.RepoCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepoRegistrationFacade {
    private final RepoCommandService repoCommandService;
    private final CommitSyncFacade commitSyncFacade;

    /**
     * 레포지토리 등록 + 초기 동기화
     * <p>
     * 1. Repo 저장 2. 저장 성공 + 초기 동기화 미완료 시 -> 전체 커밋 동기화
     */
    public void register(Long userId, RepoRegisterRequest request) {
        // 1. Repo 저장
        RepoRegistrationResult result = repoCommandService.registerRepository(userId, request);

        // 2. 중복 등록이면 동기화 넘어감
        if (result == null) {
            return;
        }

        // 3. 이미 동기화 완료된 사용자면 넘어감
        if (result.user().getLastSyncAt() != null) {
            log.info("사용자 {}는 이미 초기 동기화를 완료했습니다.", userId);
            return;
        }

        // 4. 전체 커밋 동기화
        log.info("초기 동기화 시작: userId={}, repo={}", userId, result.repo().getName());
        commitSyncFacade.syncAllCommits(result.user(), result.user().getUsername(), result.repo().getName());
    }
}
