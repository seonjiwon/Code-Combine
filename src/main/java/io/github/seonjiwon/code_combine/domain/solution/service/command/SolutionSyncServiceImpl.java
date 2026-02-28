package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.service.ProblemCommandService;
import io.github.seonjiwon.code_combine.domain.repo.code.RepoErrorCode;
import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.repo.repository.RepoRepository;
import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemInfo;
import io.github.seonjiwon.code_combine.domain.solution.utils.BaekjoonFilePathParser;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.domain.user.service.TokenService;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import io.github.seonjiwon.code_combine.global.infra.github.GitHubFetcher;
import io.github.seonjiwon.code_combine.global.infra.github.dto.GitHubCommitDetail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SolutionSyncServiceImpl implements SolutionSyncService {

    private final GitHubFetcher fetcher;
    private final BaekjoonFilePathParser filePathParser;
    private final ProblemCommandService problemCommandService;
    private final TokenService tokenService;

    private final UserRepository userRepository;
    private final RepoRepository repoRepository;
    private final SolutionRepository solutionRepository;

    @Override
    public void syncTodaySolutions(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Repo repo = repoRepository.findByUserId(userId)
                                  .orElseThrow(() -> new CustomException(RepoErrorCode.REPO_NOT_FOUND));

        String owner = user.getUsername();
        String repoName = repo.getName();
        String token = tokenService.getActiveToken(userId);

        log.info("=== 커밋 동기화 시작: owner={} ===", user.getUsername());

        List<String> commitShas = fetcher.fetchTodayCommitShas(token, owner, repoName);
        log.info("오늘의 커밋 {} 개 발견", commitShas.size());

        commitShas.forEach(sha -> syncCommit(user, token, owner, repoName, sha));

        log.info("=== 오늘의 커밋 동기화 완료 ===");
    }


    @Override
    public void syncCommit(User user, String token, String owner, String repo, String commitSha) {
        // 1. 중복 체크
        if (solutionRepository.existsByCommitSha(commitSha)) {
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

        // 5. Problem 조회 또는 생성
        Problem problem = problemCommandService.findOrCreateProblem(problemInfo);

        // 6. 소스 코드 가져오기
        String sourceCode = fetcher.fetchFileContent(token, owner, repo, sourceCodePath, commitSha);

        // 7. Solution 저장
        saveSolution(user, problem, problemInfo, sourceCode, commitSha, sourceCodePath, commitDetail);
    }

    /**
     * 소스 코드 파일 경로 찾기
     * README.md를 제외한 첫 번째 파일
     */
    private String findSourceCodePath(List<String> filePaths) {
        return filePaths.stream()
            .filter(path -> !path.endsWith("README.md"))
            .findFirst()
            .orElse(null);
    }

    /**
     * Solution 저장
     */
    private void saveSolution(User user, Problem problem, ProblemInfo problemInfo,
                              String sourceCode, String commitSha, String filePath,
                              GitHubCommitDetail commitDetail) {
        Solution solution = Solution.builder()
            .user(user)
            .problem(problem)
            .language(problemInfo.getLanguage())
            .sourceCode(sourceCode)
            .commitSha(commitSha)
            .filePath(filePath)
            .solvedAt(commitDetail.commitDate())
            .build();

        solutionRepository.save(solution);
        log.info("풀이 저장 완료: 문제 번호={}, 사용자={}, 언어={}",
            problem.getProblemNumber(), user.getUsername(), problemInfo.getLanguage());
    }
}
