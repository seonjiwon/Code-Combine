package io.github.seonjiwon.code_combine.domain.solution.service.query;


import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.solution.dto.UserCommit;
import io.github.seonjiwon.code_combine.domain.solution.dto.WeeklyCommitInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.WeeklyStat;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final UserRepository userRepository;
    private final SolutionRepository solutionRepository;

    public WeeklyCommitInfo getWeeklyCommitInfo() {
        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime sunday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
//        LocalDateTime nextSunday = sunday.plusDays(7);

        // 테스트용 하드 코딩 값
        LocalDateTime sunday = LocalDateTime.of(2026, 1, 11, 15, 0, 0);    // 일요일 KST 00:00 → UTC
        LocalDateTime nextSunday = LocalDateTime.of(2026, 1, 18, 15, 0, 0); // 다음 일요일 KST 00:00 → UTC

        List<Solution> solutions = solutionRepository.findBySolvedAtGreaterThanEqualAndSolvedAtLessThan(
            sunday, nextSunday);

        // 1. 일 ~ 토 사이의 모든 solution 을 가져옴
        Map<LocalDate, Map<Long, Long>> dailyUserCommitCounts = solutions.stream()
                                                                         .collect(
                                                                             Collectors.groupingBy(
                                                                                 solution -> solution.getSolvedAt()
                                                                                                     .toLocalDate(),
                                                                                 Collectors.groupingBy(
                                                                                     solution -> solution.getUser()
                                                                                                         .getId(),
                                                                                     Collectors.counting()
                                                                                 )
                                                                             ));

        // 2. 모든 사용자를 가져와서 Map 객체로 변환
        List<User> allUser = userRepository.findAll();
        Map<Long, User> userMap = allUser.stream()
                                         .collect(Collectors.toMap(
                                             User::getId,
                                             user -> user
                                         ));

        // 3. 변환
        List<WeeklyStat> weeklyStats = new ArrayList<>();

        // 3-1. 각 요일 마다 사용자 정보 + 커밋 수 가져오기
        for (int i = 0; i < 7; i++) {
            LocalDate date = sunday.plusDays(i).toLocalDate();
            Map<Long, Long> userCommitCount = dailyUserCommitCounts.getOrDefault(date, Map.of());
            List<UserCommit> userCommits = new ArrayList<>();

            for (Long userId : userCommitCount.keySet()) {
                User user = userMap.get(userId);
                UserCommit userCommit = UserCommit.builder()
                                                  .userId(userId)
                                                  .username(user.getUsername())
                                                  .avatarUrl(user.getAvatarUrl())
                                                  .commitCount(
                                                      userCommitCount.get(userId).intValue())
                                                  .build();

                userCommits.add(userCommit);
            }

            WeeklyStat weeklyStat = WeeklyStat.builder()
                                              .date(date)
                                              .dailyTotalUser(userCommits.size())
                                              .userCommits(userCommits)
                                              .build();

            weeklyStats.add(weeklyStat);
        }

        return WeeklyCommitInfo.builder()
                               .weeklyStats(weeklyStats)
                               .build();

    }
}
