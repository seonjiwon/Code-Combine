package io.github.seonjiwon.code_combine.global.infra.github.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GitHubCommitDetail(
    String sha,
    LocalDateTime commitDate,
    List<String> filePaths
) {
}
