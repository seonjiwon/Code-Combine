package io.github.seonjiwon.code_combine.domain.repo.service;

import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.repo.repository.RepoRepository;
import io.github.seonjiwon.code_combine.domain.solution.service.command.InitialSyncService;
import io.github.seonjiwon.code_combine.domain.user.dto.UserRepoInfo;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RepoService {

    private final RepoRepository repoRepository;
    private final UserService userService;
    private final InitialSyncService initialSyncService;

    /**
     * 사용자 Repository 등록 및 초기 동기화
     */
    public void registerRepository(Long userId, UserRepoInfo userRepoInfo) {
        User user = userService.findUserById(userId);

        // 중복 등록 체크
        if (repoRepository.existsByUserId(userId)) {
            log.warn("사용자 {}는 이미 Repository에 등록되어 있습니다", userId);
            return;
        }

        // Repository 저장
        Repo repo = createRepo(user, userRepoInfo);
        repoRepository.save(repo);
        log.info("Repository 등록 완료: userId={}, repoName={}", userId, repo.getName());

        // 초기 동기화 트리거
        triggerInitialSync(user, repo);
    }

    /**
     * Repository 엔티티 생성
     */
    private Repo createRepo(User user, UserRepoInfo userRepoInfo) {
        return Repo.builder()
            .user(user)
            .name(userRepoInfo.getName())
            .build();
    }

    /**
     * 초기 동기화 트리거
     */
    private void triggerInitialSync(User user, Repo repo) {
        if (user.getLastSyncAt() != null) {
            log.info("사용자 {}는 이미 동기화를 완료했습니다", user.getId());
            return;
        }

        log.info("초기 동기화 시작: userId={}, repo={}", user.getId(), repo.getName());
        initialSyncService.syncAllCommits(user, user.getUsername(), repo.getName());
    }
}
