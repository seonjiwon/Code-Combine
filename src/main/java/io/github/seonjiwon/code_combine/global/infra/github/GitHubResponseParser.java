package io.github.seonjiwon.code_combine.global.infra.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import io.github.seonjiwon.code_combine.global.external.exception.GitErrorCode;
import io.github.seonjiwon.code_combine.global.infra.github.dto.GitHubCommitDetail;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubResponseParser {

    private final ObjectMapper objectMapper;

    /**
     * GitHub 레포지토리 목록에서 name만 추출
     */
    public List<String> parseRepos(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            List<String> repoNames = new ArrayList<>();

            for (JsonNode repoNode : root) {
                repoNames.add(repoNode.get("name").asText());
            }

            log.info("레포지토리 {} 개 파싱 완료", repoNames.size());
            return repoNames;
        } catch (JsonProcessingException e) {
            log.error("레포지토리 목록 파싱 실패", e);
            throw new CustomException(GitErrorCode.GITHUB_JSON_PARSE_ERROR);
        }
    }

    public List<String> parseCommitShas(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            List<String> shas = new ArrayList<>();

            for (JsonNode commitNode : root) {
                String sha = commitNode.get("sha").asText();
                shas.add(sha);
            }
            return shas;
        } catch (JsonProcessingException e) {
            log.error("커밋 SHA 파싱 실패", e);
            throw new CustomException(GitErrorCode.GITHUB_JSON_PARSE_ERROR);
        }
    }

    public GitHubCommitDetail parseCommitDetail(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 1. 커밋 SHA
            String sha = root.get("sha").asText();

            // 2. 커밋 날짜 파싱
            String dateStr = root.get("commit").get("author").get("date").asText();
            LocalDateTime commitDate = LocalDateTime.parse(
                dateStr,
                DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Asia/Seoul"))
            );

            // 3. 파일 경로 파싱
            JsonNode filesNode = root.get("files");
            List<String> filePaths = new ArrayList<>();

            for (JsonNode fileNode : filesNode) {
                String filename = fileNode.get("filename").asText();
                filePaths.add(filename);
            }

            return GitHubCommitDetail.builder()
                .sha(sha)
                .commitDate(commitDate)
                .filePaths(filePaths)
                .build();
        } catch (JsonProcessingException e) {
            log.error("커밋 상세 정보 파싱 실패", e);
            throw new CustomException(GitErrorCode.GITHUB_JSON_PARSE_ERROR);
        }
    }
}
