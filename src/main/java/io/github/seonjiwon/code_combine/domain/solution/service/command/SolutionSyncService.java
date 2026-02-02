package io.github.seonjiwon.code_combine.domain.solution.service.command;

import io.github.seonjiwon.code_combine.domain.user.domain.User;

public interface SolutionSyncService {

    void syncTodaySolutions(User userId, String owner, String repo);
}
