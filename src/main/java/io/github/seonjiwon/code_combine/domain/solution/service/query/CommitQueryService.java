package io.github.seonjiwon.code_combine.domain.solution.service.query;


import static io.github.seonjiwon.code_combine.domain.solution.dto.DashboardResponse.*;

import io.github.seonjiwon.code_combine.domain.solution.dto.DailyUserCommitCount;
import io.github.seonjiwon.code_combine.domain.solution.dto.DashboardResponse.UserCommit;
import io.github.seonjiwon.code_combine.domain.solution.dto.DashboardResponse.WeeklyCommitInfo;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommitQueryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final SolutionRepository solutionRepository;

    public WeeklyCommitInfo getWeeklyCommitInfo() {
        // 1. 이번 주 날짜 범위 계산
        LocalDate[] weekRange = calculateWeekRange();
        LocalDate startDate = weekRange[0];
        LocalDate endDate = weekRange[1];

        LocalDateTime start = startDate.atStartOfDay(KST).toLocalDateTime();
        LocalDateTime end = endDate.atStartOfDay(KST).toLocalDateTime();

        // 2. 날짜별/유저별 커밋 수 조회 - left join
        List<DailyUserCommitCount> commitCounts = solutionRepository.findDailyUserCommitCounts(
            start, end);

        // 3. 날짜별로 그룹핑
        Map<LocalDate, List<DailyUserCommitCount>> grouped = commitCounts.stream()
                                                                         .collect(Collectors.groupingBy(
                                                                                 commitCount -> commitCount.getSolvedAt()
                                                                                                           .toLocalDate()));

        // 4. 응답 DTO 변환
        return convertWeeklyStats(startDate, grouped);
    }

    // 이번 주 일~토 날짜 범위 계산 (KST 기준)
    private LocalDate[] calculateWeekRange() {
        LocalDate today = LocalDate.now(KST);
        // 오늘 날짜 기준 이번주 일요일 구함
        LocalDate sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // 다음주 일요일 구함 + 7
        LocalDate nextSunday = sunday.plusDays(7);
        return new LocalDate[]{sunday, nextSunday};
    }

    // 집계 데이터를 응답 DTO로 변환
    private WeeklyCommitInfo convertWeeklyStats(LocalDate startDate,
                                                Map<LocalDate, List<DailyUserCommitCount>> grouped) {
        List<WeeklyState> weeklyStats = new ArrayList<>();

        // 일 ~ 토 순회
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            List<DailyUserCommitCount> dailyCounts = grouped.getOrDefault(date, List.of());

            List<UserCommit> userCommits = dailyCounts.stream()
                                                      .map(UserCommit::convertToUserCommit)
                                                      .toList();

            weeklyStats.add(WeeklyState.convertToWeeklyState(date, userCommits));
        }

        return WeeklyCommitInfo.convertToWeeklyCommitInfo(weeklyStats);
    }
}
