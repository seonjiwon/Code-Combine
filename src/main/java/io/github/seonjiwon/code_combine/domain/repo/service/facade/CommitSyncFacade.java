package io.github.seonjiwon.code_combine.domain.repo.service.facade;

import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemInfo;
import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.repo.service.RepoQueryService;
import io.github.seonjiwon.code_combine.domain.solution.service.command.SolutionSyncService;
import io.github.seonjiwon.code_combine.domain.solution.utils.BaekjoonFilePathParser;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.service.TokenService;
import io.github.seonjiwon.code_combine.domain.user.service.UserQueryService;
import io.github.seonjiwon.code_combine.global.infra.github.GitHubFetcher;
import io.github.seonjiwon.code_combine.global.infra.github.dto.GitHubCommitDetail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommitSyncFacade {

    private final GitHubFetcher fetcher;
    private final BaekjoonFilePathParser filePathParser;

    private final SolutionSyncService solutionSyncService;
    private final TokenService tokenService;
    private final UserQueryService userQueryService;
    private final RepoQueryService repoQueryService;

    /**
     * 전체 커밋 동기화 - 레포 등 시 호출 모드 커밋 Sha 를 오래된 것 부터 조회하여 순차 동기화
     */
    public void syncAllCommits(User user, String owner, String repoName) {
        log.info("전체 커밋 동기화 시작: owner={}, repo={}", owner, repoName);

        String token = tokenService.getActiveToken(user.getId());
        List<String> allCommitShas = fetcher.fetchAllCommitShas(token, owner, repoName);

        allCommitShas.forEach(sha -> {
            try {
                syncSingleCommit(user, token, owner, repoName, sha);
            } catch (Exception e) {
                log.info("커밋 동기화 실패: sha={}, error={}", sha, e.getMessage());
            }
        });

        solutionSyncService.updateUserSyncStatus(user);
        log.info("전체 커밋 동기화 완료");
    }

    /**
     * 오늘 커밋 동기화 - 일일 스케줄러에서 호출
     */
    public void syncTodayCommits(Long userId) {
        User user = userQueryService.getById(userId);
        Repo repo = repoQueryService.getByUserId(userId);

        String owner = user.getUsername();
        String repoName = repo.getName();
        String token = tokenService.getActiveToken(userId);

        log.info("오늘 커밋 동기화 시작: owner={}", owner);

        List<String> commitShas = fetcher.fetchTodayCommitShas(token, owner, repoName);

        commitShas.forEach(sha -> {
            try {
                syncSingleCommit(user, token, owner, repoName, sha);
            } catch (Exception e) {
                log.info("커밋 동기화 실패: sha={}, error={}", sha, e.getMessage());
            }
        });

        log.info("오늘 커밋 동기화 완료");
    }

    /**
     * 한 개의 커밋 동기화
     */
    private void syncSingleCommit(User user, String token, String owner, String repo,
                                  String commitSha) {
        // 1. 중복 체크
        if (solutionSyncService.existsByCommitSha(commitSha)) {
            log.info("이미 동기화된 커밋: {}", commitSha);
            return;
        }

        // 2. 커밋 상세 정보 조회
        GitHubCommitDetail commitDetail = fetcher.fetchCommitDetail(token, owner, repo, commitSha);

        // 3. 소스 코드 파일 찾기
        String sourceCodePath = findSourceCodePath(commitDetail.filePaths());
        if (sourceCodePath == null) {
            log.info("소스 코드 파일 없음: commitSha={}", commitSha);
            return;
        }

        // 4. 파일 경로에서 문제 정보 파싱
        ProblemInfo problemInfo = filePathParser.parse(sourceCodePath);

        // 5. 소스 코드 가져오기
        String sourceCode = fetcher.fetchFileContent(token, owner, repo, sourceCodePath, commitSha);

        // 6. DB 저장
        solutionSyncService.saveSolution(user, problemInfo, sourceCode,
            commitSha, sourceCodePath, commitDetail);
    }

    /**
     * 소스 코드 파일 경로 찾기. README.md를 제외한 첫 번째 파일
     */
    private String findSourceCodePath(List<String> filePaths) {
        return filePaths.stream()
                        .filter(path -> !path.startsWith("README.md"))
                        .findFirst()
                        .orElse(null);
    }

}
