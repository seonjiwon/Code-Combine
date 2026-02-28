package io.github.seonjiwon.code_combine.global.infra.github;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubApiClient {

    private final RestClient gitHubRestClient;

    @Value("${github.raw-accept-header}")
    private String rawAcceptHeader;

    @Value("${github.json-accept-header}")
    private String jsonAcceptHeader;

    /**
     * 인증된 사용자의 레포지토리 목록 조회
     */
    public String getUserRepos(String token) {
        String url = "/user/repos?type=owner&per_page=100&sort=updated";
        log.info("GitHub API 호출 - 레포 목록 조회");
        return fetchAsJson(url, token);
    }

    /**
     * 특정 기간의 커밋 목록 조회
     */
    public String getCommits(String token, String owner, String repo, ZonedDateTime since,
                             ZonedDateTime until) {
        String sinceStr = since.format(DateTimeFormatter.ISO_INSTANT);
        String untilStr = until.format(DateTimeFormatter.ISO_INSTANT);

        String url = String.format(
            "/repos/%s/%s/commits?since=%s&until=%s",
            owner, repo, sinceStr, untilStr
        );

        log.info("GitHub API 호출: {}", url);
        return fetchAsJson(url, token);
    }

    /**
     * 모든 커밋 목록 조회 (페이지네이션)
     */
    public String getCommits(String token, String owner, String repo, int page, int perPage) {
        String url = String.format(
            "/repos/%s/%s/commits?page=%d&per_page=%d",
            owner, repo, page, perPage
        );

        log.info("GitHub API 호출 (page {}): {}", page, url);
        return fetchAsJson(url, token);
    }

    /**
     * 커밋 상세 정보 조회
     */
    public String getCommitDetail(String token, String owner, String repo, String sha) {
        log.info("커밋 세부 정보 가져오기");

        String url = String.format(
            "/repos/%s/%s/commits/%s",
            owner, repo, sha
        );

        return fetchAsJson(url, token);
    }

    /**
     * 파일 내용 조회
     */
    public String getFileContent(String token, String owner, String repo, String path, String ref) {
        String url = String.format(
            "/repos/%s/%s/contents/%s?ref=%s",
            owner, repo, path, ref
        );

        log.info("GitHub API 호출: {}", url);
        return fetchAsRaw(url, token);
    }

    private String fetchAsJson(String url, String token) {
        return gitHubRestClient.get()
                               .uri(url)
                               .header("Authorization", "Bearer " + token)
                               .header("Accept", jsonAcceptHeader)
                               .retrieve()
                               .body(String.class);
    }

    private String fetchAsRaw(String url, String token) {
        return gitHubRestClient.get()
                               .uri(url)
                               .header("Authorization", "Bearer " + token)
                               .header("Accept", rawAcceptHeader)
                               .retrieve()
                               .body(String.class);
    }
}
