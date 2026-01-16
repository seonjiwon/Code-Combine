package io.github.seonjiwon.code_combine.global.external.github;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class GitFileClientTest {

    @Autowired
    GitClient gitClient;


    private static final String OWNER = "seonjiwon";
    private static final String REPO = "Java-Algorithm";
    private static final String SHA = "e57ecf7aa4f57934d8311edb3ce1afa1ec75e122";

    @Test
    @DisplayName("특정 날짜의 커밋 SHA 목록을 가져옴")
    void fetchCommitShasWhenValidDate() {
        // given
        LocalDateTime since = LocalDateTime.of(2026, 1, 6, 0, 0, 0);
        LocalDateTime until = LocalDateTime.of(2026, 1, 6, 23, 59, 59);

        // when
        List<String> commitShas = gitClient.fetchCommitShas(OWNER, REPO, since, until);

        // then
        assertThat(commitShas)
            .isNotNull()
            .isNotEmpty()
            .contains("e57ecf7aa4f57934d8311edb3ce1afa1ec75e122");

        log.info("Commit SHAs: {}", commitShas);
        log.info("Total commits: {}", commitShas.size());
    }

    @Test
    @DisplayName("올바른 SHA를 입력했을 시 파일명 목록을 가져옴")
    void fetchFilenamesWhenCorrectSha() {
        // given
        String sha = "e57ecf7aa4f57934d8311edb3ce1afa1ec75e122";

        // when
        List<String> filenames = gitClient.fetchFilePath(OWNER, REPO, sha);

        // then
        assertThat(filenames)
            .isNotNull()
            .hasSize(2)
            .anyMatch(name -> name.contains("README.md"))
            .anyMatch(name -> name.contains(".java"));

        log.info("Filenames: {}", filenames);
        filenames.forEach(name -> log.info("  - {}", name));
    }

    @Test
    @DisplayName("파일명 목록에서 Java 파일만 필터링")
    void filterJavaFilesFromFilenames() {
        // given
        String sha = "e57ecf7aa4f57934d8311edb3ce1afa1ec75e122";

        // when
        List<String> filenames = gitClient.fetchFilePath(OWNER, REPO, sha);
        List<String> javaFiles = filenames.stream()
                                          .filter(name -> name.endsWith(".java"))
                                          .toList();

        // then
        assertThat(javaFiles)
            .hasSize(1)
            .allMatch(name -> name.endsWith(".java"));

        log.info("Java files: {}", javaFiles);
    }

    @Test
    @DisplayName("올바른 경로를 입력했을 시 소스코드를 가져옴")
    void fetchSourceCodeWhenCorrectPath() {
        // given
        String path = "/백준/Gold/22942.\u2005데이터\u2005체커/데이터\u2005체커.java";
        String language = "java";

        // when
        String sourceCode = gitClient.fetchSourceCode(OWNER, REPO, path, SHA);

        // then
        assertThat(sourceCode)
            .isNotNull()
            .isNotEmpty()
            .contains("import")
            .contains("class Main")
            .contains("public static void main");

        log.info("Source code length: {}", sourceCode.length());
    }

    @Test
    @DisplayName("올바른 경로를 입력했을 시 README를 가져옴")
    void fetchReadmeWhenCorrectPath() {
        // given
        String path = "/백준/Gold/22942.\u2005데이터\u2005체커/README.md";

        // when
        String readme = gitClient.fetchReadme(OWNER, REPO, path, SHA);

        // then
        assertThat(readme)
            .isNotNull()
            .isNotEmpty()
            .contains("22942");

        log.info("README length: {}", readme.length());
    }

    @Test
    @DisplayName("전체 흐름 테스트")
    void fullWorkFlow() throws Exception{
        // given
        LocalDateTime since = LocalDateTime.of(2026, 1, 11, 15, 0, 0);
        LocalDateTime until = LocalDateTime.of(2026, 1, 12, 14, 59, 59);

        // when
        List<String> commitShas = gitClient.fetchCommitShas(OWNER, REPO, since, until);
        assertThat(commitShas).isNotEmpty();
        log.debug("commitShas: {}", commitShas.toString());

        String firstSha = commitShas.get(0);
        List<String> filenames = gitClient.fetchFilePath(OWNER, REPO, firstSha);
        assertThat(filenames).isNotEmpty();

        log.debug("filename: {}",filenames.toString());

        List<String> javaFiles = filenames.stream()
                                     .filter(name -> name.endsWith(".java"))
                                     .toList();
        assertThat(javaFiles).isNotEmpty();

        // then
        String firstJavaFile = javaFiles.get(0);
        String sourceCode = gitClient.fetchSourceCode(OWNER, REPO, firstJavaFile, firstSha);

        assertThat(sourceCode)
            .isNotNull()
            .contains("import");
        log.debug(sourceCode);
    }

}