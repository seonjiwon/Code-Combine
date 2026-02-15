package io.github.seonjiwon.code_combine.global.infra.github;

import io.github.seonjiwon.code_combine.global.infra.github.dto.GitHubCommitDetail;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubCommitFetcher {

    private final GitHubApiClient apiClient;
    private final GitHubResponseParser parser;

    private static final int MAX_PER_PAGE = 100;

    /**
     * 오늘 날짜의 커밋 SHA 목록 조회
     */
    public List<String> fetchTodayCommitShas(String owner, String repo) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();

        // UTC 기준으로 9시간 차감 (KST -> UTC)
        LocalDateTime since = todayStart.minusHours(9);
        LocalDateTime until = todayEnd.minusHours(9);

        log.info("오늘 커밋 조회: {}/{}, 기간: {} ~ {}", owner, repo, since, until);

        String response = apiClient.getCommits(owner, repo, since, until);
        List<String> commitShas = parser.parseCommitShas(response);

        log.info("오늘 커밋 {} 개 발견", commitShas.size());
        return commitShas;
    }

    /**
     * 모든 커밋 SHA 목록 조회 (페이지네이션)
     */
    public List<String> fetchAllCommitShas(String owner, String repo) {
        List<String> allCommitShas = new ArrayList<>();
        int page = 1;

        while (true) {
            String response = apiClient.getCommits(owner, repo, page, MAX_PER_PAGE);
            List<String> pageShas = parser.parseCommitShas(response);

            if (pageShas.isEmpty()) {
                break;
            }

            allCommitShas.addAll(pageShas);
            log.info("Page {} 조회 완료: {} 개 커밋 (누적: {})", page, pageShas.size(), allCommitShas.size());

            // 마지막 페이지 체크
            if (pageShas.size() < MAX_PER_PAGE) {
                break;
            }

            page++;
        }

        // 오래된 커밋부터 처리하도록 역순 정렬
        Collections.reverse(allCommitShas);

        log.info("전체 커밋 조회 완료: 총 {} 개", allCommitShas.size());
        return allCommitShas;
    }

    /**
     * 커밋 상세 정보 조회
     */
    public GitHubCommitDetail fetchCommitDetail(String owner, String repo, String sha) {
        String response = apiClient.getCommitDetail(owner, repo, sha);
        GitHubCommitDetail detail = parser.parseCommitDetail(response);

        log.debug("커밋 상세 조회: sha={}, 파일 수={}", sha, detail.filePaths().size());
        return detail;
    }

    /**
     * 파일 내용 조회
     */
    public String fetchFileContent(String owner, String repo, String path, String ref) {
        log.debug("파일 내용 조회: path={}, ref={}", path, ref);
        return apiClient.getFileContent(owner, repo, path, ref);
    }
}
