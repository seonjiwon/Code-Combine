package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.solution.service.query.SolutionQueryService;
import io.github.seonjiwon.code_combine.domain.solution.utils.GitClient;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
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

    private final GitClient gitClient;
    private final SolutionSyncService solutionSyncService;
    private final UserRepository userRepository;

    public void syncAllCommits(User user, String owner, String repo) {
        log.info("=== 전체 동기화 시작 ===");
        log.info("User: {}, Repo: {}/{}", user.getUsername(), owner, repo);

        // 1. 전체 Commit Sha 조회
        List<String> allCommitShas = gitClient.fetchAllCommitShas(owner, repo);

        log.info("총 {} 개의 커밋을 동기화합니다.", allCommitShas.size());

        // 2. 각 커밋을 순차적으로 동기화
        allCommitShas.forEach(commitSha -> solutionSyncService.syncCommit(user, owner, repo, commitSha));

        // 3. 동기화 완료 후 lasySyncAt 업데이트
        user.updateLastSyncAt();
        userRepository.save(user);

        log.info("=== 전체 동기화 완료 ===");
    }
}
