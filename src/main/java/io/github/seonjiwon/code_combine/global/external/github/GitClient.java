package io.github.seonjiwon.code_combine.global.external.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import io.github.seonjiwon.code_combine.global.external.exception.GitErrorCode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${github.base-url}")
    private String baseUrl;

    @Value("${github.token}")
    private String token;

    @Value("${github.raw-accept-header}")
    private String rawAcceptHeader;

    @Value("${github.json-accept-header}")
    private String jsonAcceptHeader;


    public List<String> fetchCommitShas(
        String owner,
        String repo,
        LocalDateTime since,
        LocalDateTime until
    ) {
        ZonedDateTime sinceUtc = since.atZone(ZoneOffset.UTC);
        ZonedDateTime untilUtc = until.atZone(ZoneOffset.UTC);

        String url = String.format("%s/repos/%s/%s/commits?since=%s&until=%s", baseUrl, owner, repo,
            sinceUtc, untilUtc);

        log.info("Fetching commits: {}", url);

        String response = fetchContentAsJson(url);

        return parseCommitSha(response);
    }


    public List<String> fetchFilePath(String owner, String repo, String sha) {
        String url = String.format("%s/repos/%s/%s/commits/%s", baseUrl, owner, repo, sha);

        log.info("Fetching commit files: {}", url);

        String response = fetchContentAsJson(url);

        return parseFilenames(response);
    }


    public String fetchSourceCode(String owner, String repo, String path, String ref) {
        String url = buildFileUrl(owner, repo, path, ref);

        log.info("Fetching source code: {}", url);

        return fetchContent(url);
    }

    public String fetchReadme(String owner, String repo, String path, String ref) {
        String url = buildFileUrl(owner, repo, path + "", ref);

        log.info("Fetching README: {}", url);

        return fetchContent(url);
    }

    private String buildFileUrl(String owner, String repo, String path, String ref) {
        return String.format("%s/repos/%s/%s/contents/%s?ref=%s", baseUrl, owner, repo, path, ref);
    }

    private String fetchContent(String url) {
        return restClient.get()
                         .uri(url)
                         .header("Authorization", "Bearer " + token)
                         .header("Accept", rawAcceptHeader)
                         .retrieve()
                         .body(String.class);
    }

    private String fetchContentAsJson(String url) {
        return restClient.get()
                         .uri(url)
                         .header("Authorization", "Bearer " + token)
                         .header("Accept", jsonAcceptHeader)
                         .retrieve()
                         .body(String.class);
    }

    private List<String> parseCommitSha(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            List<String> shas = new ArrayList<>();

            for (JsonNode commitNode : root) {
                String sha = commitNode.get("sha").asText();
                shas.add(sha);
            }
            return shas;
        } catch (JsonProcessingException e) {
            throw new CustomException(GitErrorCode.GITHUB_JSON_PARSE_ERROR);
        }
    }

    private List<String> parseFilenames(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode filesNode = root.get("files");

            List<String> filenames = new ArrayList<>();

            for (JsonNode fileNode : filesNode) {
                String filename = fileNode.get("filename").asText();
                filenames.add(filename);
            }
            return filenames;
        } catch (JsonProcessingException e) {
            throw new CustomException(GitErrorCode.GITHUB_JSON_PARSE_ERROR);
        }
    }

}
