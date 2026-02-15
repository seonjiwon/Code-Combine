package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyUserCommitCount {
    private LocalDateTime solvedAt;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Long commitCount;
}
