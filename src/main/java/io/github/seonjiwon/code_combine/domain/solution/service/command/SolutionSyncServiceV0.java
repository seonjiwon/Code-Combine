package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.problem.domain.Problem;
import io.github.seonjiwon.code_combine.domain.problem.repository.ProblemRepository;
import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.solution.dto.CommitDetail;
import io.github.seonjiwon.code_combine.domain.solution.dto.ProblemInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionData;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SolutionSyncServiceV0 implements SolutionSyncService {

    private final SolutionCollectService solutionCollectService;
    private final SolutionRepository solutionRepository;
    private final ProblemRepository problemRepository;

    @Override
    public void syncTodaySolutions(User user, String owner, String repo) {
        // 1. Collect 서비스로 부터 커밋 목록 조회
        List<String> commitShas = solutionCollectService.fetchCommitShas(owner, repo);
        log.info("{} 개의 커밋을 찾았습니다.", commitShas.size());

        // 2. 각 커밋 동기화
        for (String commitSha : commitShas) {
            syncCommit(user, owner, repo, commitSha);
        }
    }

    public void syncCommit(User user, String owner, String repo, String commitSha) {
        // 1. 이미 존재하는 커밋인지 확인
        if (solutionRepository.existsByCommitSha(commitSha)) {
            log.info("커밋이 이미 동기화 되었습니다: {}", commitSha);
            return;
        }

        // 2. Collect 서비스로부터 파일 목록 조회
        CommitDetail commitDetail = solutionCollectService.fetchCommitDetail(owner, repo,
            commitSha);

        String readmePath = null;
        String sourceCodePath = null;

        for (String filePath : commitDetail.getFilePaths()) {
            if (filePath.endsWith("README.md")) {
                readmePath = filePath;
            } else {
                sourceCodePath = filePath;
            }
        }

        // 3. 소스코드 + readme 가져오기
        SolutionData solutionData = solutionCollectService.fetchSolutionData(owner, repo,
            sourceCodePath, readmePath,
            commitSha);

        // 4. 문제 번호 추출
        ProblemInfo problemInfo = extractProblemInfo(sourceCodePath);

        // 5. Problem 찾거나 생성
        Problem problem = problemRepository.findByProblemNumber(problemInfo.getProblemNumber())
                                           .orElseGet(() -> problemRepository.save(
                                               Problem.builder()
                                                      .problemNumber(problemInfo.getProblemNumber())
                                                      .title(problemInfo.getTitle())
                                                      .problemUrl("https://www.acmicpc.net/problem/"
                                                          + problemInfo.getProblemNumber())
                                                      .build()
                                           ));

        // 6. Solution 저장
        Solution solution = Solution.builder()
                                    .user(user)
                                    .problem(problem)
                                    .language(problemInfo.getLanguage())
                                    .sourceCode(solutionData.getSourceCode())
                                    .commitSha(commitSha)
                                    .filePath(sourceCodePath)
                                    .solvedAt(commitDetail.getCommitDate())
                                    .build();

        solutionRepository.save(solution);
        log.info("풀이 저장: 문제 {}", problemInfo.getProblemNumber());
    }

    private ProblemInfo extractProblemInfo(String filePath) {
        int lastSlash = filePath.lastIndexOf('/');
        int secondLastSlash = filePath.lastIndexOf('/', lastSlash - 1);

        // 1. 문제 문장 추출 (11286.절댓값 힙)
        String problemDir = filePath.substring(secondLastSlash + 1, lastSlash);

        int dotIndex = problemDir.indexOf('.');

        // 2. 문제 번호, 제목 추출
        int problemNumber = Integer.parseInt(problemDir.substring(0, dotIndex).trim());
        String title = problemDir.substring(dotIndex + 1).trim();

        // 3. 언어 추출
        int languageDotIndex = filePath.lastIndexOf('.');
        String language = filePath.substring(languageDotIndex + 1);

        return ProblemInfo.builder()
                          .problemNumber(problemNumber)
                          .title(title)
                          .language(language)
                          .build();
    }
}
