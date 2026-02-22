package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record DailyUserCommitCountProjection(
    LocalDateTime solvedAt,
    Long userId,
    String username,
    String avatarUrl,
    Long commitCount
) {

}
