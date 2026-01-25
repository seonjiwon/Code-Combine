package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.solution.converter.GitResponseConverter;
import io.github.seonjiwon.code_combine.domain.solution.dto.CommitInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.FileInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionData;
import io.github.seonjiwon.code_combine.global.external.github.GitClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionCollectService {

    private final GitClient gitClient;


    public List<CommitInfo> fetchCommitShas(String owner, String repo) {
        // 1. 한국 시간(KST) 기준으로 '오늘'의 시작과 끝을 잡습니다.
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

        // 2. GitHub API(UTC) 기준에 맞게 9시간을 뺍니다.
        LocalDateTime since = todayStart.minusHours(9);
        LocalDateTime until = todayEnd.minusHours(9);


        // 테스트용 하드 코딩 값
//        LocalDateTime since = LocalDateTime.of(2026, 1, 11, 15, 0, 0);
//        LocalDateTime until = LocalDateTime.of(2026, 1, 12, 14, 59, 59);

        List<String> commitShas = gitClient.fetchCommitShas(owner, repo, since, until);

        log.info("Fetch Commit Response: {}", commitShas);

        return GitResponseConverter.toCommitList(commitShas);
    }

    public List<FileInfo> fetchFilePaths(String owner, String repo, String commitSha) {
        List<String> filePaths = gitClient.fetchFilePath(owner, repo, commitSha);

        log.info("Fetch FilePath Response: {}", filePaths);

        return GitResponseConverter.toFileInfos(filePaths);
    }

    public SolutionData fetchSolutionData(String owner, String repo, String sourceCodePath,
                                          String readmePath, String commitSha) {
        String sourceCode = gitClient.fetchSourceCode(owner, repo, sourceCodePath, commitSha);
        String readMe = gitClient.fetchSourceCode(owner, repo, readmePath, commitSha);

        log.info("Fetch SourceCode: {}", sourceCode);
        log.info("Fetch ReadMe: {}", readMe);

        return GitResponseConverter.toSolutionData(sourceCode, readMe);
    }
}
