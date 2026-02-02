package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyCommitInfo {
    private List<WeeklyStat> weeklyStats;
}
