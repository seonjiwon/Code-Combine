package io.github.seonjiwon.code_combine.domain.solution.service;

import io.github.seonjiwon.code_combine.domain.user.entity.User;

public interface SolutionSyncService {

    void syncTodaySolutions(User userId, String owner, String repo);
}
