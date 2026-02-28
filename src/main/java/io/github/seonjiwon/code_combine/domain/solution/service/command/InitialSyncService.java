package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.domain.user.service.TokenService;
import io.github.seonjiwon.code_combine.global.infra.github.GitHubFetcher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InitialSyncService {

    private final GitHubFetcher fetcher;
    private final SolutionSyncService solutionSyncService;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    /**
     * 모든 커밋 동기화
     */
    public void syncAllCommits(User user, String owner, String repo) {
        log.info("=== 전체 커밋 동기화 시작 ===");

        String token = tokenService.getActiveToken(user.getId());

        // 1. 전체 커밋 SHA 목록 조회 (오래된 것부터)
        List<String> allCommitShas = fetcher.fetchAllCommitShas(token, owner, repo);
        log.info("커밋 동기화 시작");

        // 2. 각 커밋 동기화
        allCommitShas.forEach(commitSha ->
            solutionSyncService.syncCommit(user, token, owner, repo, commitSha)
        );

        // 3. 사용자 동기화 상태 업데이트
        updateUserSyncStatus(user);

        log.info("=== 전체 커밋 동기화 완료 ===");
    }

    /**
     * 사용자 동기화 상태 업데이트
     */
    private void updateUserSyncStatus(User user) {
        user.updateLastSyncAt();
        userRepository.save(user);
        log.info("사용자 동기화 시간 업데이트: userId={}, lastSyncAt={}", user.getId(), user.getLastSyncAt());
    }
}
