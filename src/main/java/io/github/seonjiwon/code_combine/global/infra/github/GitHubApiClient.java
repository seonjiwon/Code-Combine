package io.github.seonjiwon.code_combine.global.infra.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubApiClient {

    private final RestClient restClient;

    @Value("${github.base-url}")
    private String baseUrl;

    @Value("${github.token}")
    private String token;

    @Value("${github.raw-accept-header}")
    private String rawAcceptHeader;

    @Value("${github.json-accept-header}")
    private String jsonAcceptHeader;

    /**
     * 특정 기간의 커밋 목록 조회
     */
    public String getCommits(String owner, String repo, LocalDateTime since, LocalDateTime until) {
        ZonedDateTime sinceUtc = since.atZone(ZoneOffset.UTC);
        ZonedDateTime untilUtc = until.atZone(ZoneOffset.UTC);

        String url = String.format(
            "%s/repos/%s/%s/commits?since=%s&until=%s",
            baseUrl, owner, repo, sinceUtc, untilUtc
        );

        log.debug("GitHub API 호출: {}", url);
        return fetchAsJson(url);
    }

    /**
     * 모든 커밋 목록 조회 (페이지네이션)
     */
    public String getCommits(String owner, String repo, int page, int perPage) {
        String url = String.format(
            "%s/repos/%s/%s/commits?page=%d&per_page=%d",
            baseUrl, owner, repo, page, perPage
        );

        log.debug("GitHub API 호출 (page {}): {}", page, url);
        return fetchAsJson(url);
    }

    /**
     * 커밋 상세 정보 조회
     */
    public String getCommitDetail(String owner, String repo, String sha) {
        String url = String.format(
            "%s/repos/%s/%s/commits/%s",
            baseUrl, owner, repo, sha
        );

        log.debug("GitHub API 호출: {}", url);
        return fetchAsJson(url);
    }

    /**
     * 파일 내용 조회
     */
    public String getFileContent(String owner, String repo, String path, String ref) {
        String url = String.format(
            "%s/repos/%s/%s/contents/%s?ref=%s",
            baseUrl, owner, repo, path, ref
        );

        log.debug("GitHub API 호출: {}", url);
        return fetchAsRaw(url);
    }

    private String fetchAsJson(String url) {
        return restClient.get()
            .uri(url)
            .header("Authorization", "Bearer " + token)
            .header("Accept", jsonAcceptHeader)
            .retrieve()
            .body(String.class);
    }

    private String fetchAsRaw(String url) {
        return restClient.get()
            .uri(url)
            .header("Authorization", "Bearer " + token)
            .header("Accept", rawAcceptHeader)
            .retrieve()
            .body(String.class);
    }
}
