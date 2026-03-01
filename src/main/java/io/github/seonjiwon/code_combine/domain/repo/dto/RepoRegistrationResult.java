package io.github.seonjiwon.code_combine.domain.repo.dto;

import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.user.domain.User;

public record RepoRegistrationResult(
    User user,
    Repo repo
) {

}
