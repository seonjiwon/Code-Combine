package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public class DashboardResponse {

    @Builder
    public record WeeklyCommitInfo(
        List<WeeklyState> weeklyStats
    ) {

        public static WeeklyCommitInfo convertToWeeklyCommitInfo(List<WeeklyState> weeklyStats) {
            return WeeklyCommitInfo.builder()
                .weeklyStats(weeklyStats)
                .build();
        }
    }

    @Builder
    public record WeeklyState(
        LocalDate date,
        Integer dailyTotalUser,
        List<UserCommit> userCommits
    ) {

        public static WeeklyState convertToWeeklyState(LocalDate date, List<UserCommit> userCommits) {
            return WeeklyState.builder()
                .date(date)
                .dailyTotalUser(userCommits.size())
                .userCommits(userCommits)
                .build();
        }
    }

    @Builder
    public record UserCommit(
        Long userId,
        String username,
        String avatarUrl,
        Integer commitCount
    ) {

        public static UserCommit convertToUserCommit(DailyUserCommitCount c) {
            return UserCommit.builder()
                .userId(c.getUserId())
                .username(c.getUsername())
                .avatarUrl(c.getAvatarUrl())
                .commitCount(c.getCommitCount().intValue())
                .build();
        }
    }
}
