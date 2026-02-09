package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public class DashboardResponse {

    @Builder
    public record WeeklyCommitInfo(
        List<WeeklyState> weeklyStats
    ) {

    }

    @Builder
    public record WeeklyState(
        LocalDate date,
        Integer dailyTotalUser,
        List<UserCommit> userCommits
    ) {

    }

    @Builder
    public record UserCommit(
        Long userId,
        String username,
        String avatarUrl,
        Integer commitCount
    ) {

    }
}
