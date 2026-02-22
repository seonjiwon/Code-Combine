package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.user.domain.User;

public interface SolutionSyncService {

    void syncTodaySolutions(Long userId);

    void syncCommit(User user, String token, String owner, String repo, String commitSha);
}
