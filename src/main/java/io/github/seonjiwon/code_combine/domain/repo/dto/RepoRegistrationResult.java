package io.github.seonjiwon.code_combine.domain.repo.dto;

import io.github.seonjiwon.code_combine.domain.repo.entity.Repo;
import io.github.seonjiwon.code_combine.domain.user.entity.User;

public record RepoRegistrationResult(
    User user,
    Repo repo
) {

}
