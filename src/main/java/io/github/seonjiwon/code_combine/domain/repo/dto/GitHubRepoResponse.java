package io.github.seonjiwon.code_combine.domain.repo.dto;

import java.util.List;

public record GitHubRepoResponse(
    String name
) {
    public static GitHubRepoResponse from(String name) {
        return new GitHubRepoResponse(name);
    }

    public record RepoList(
        List<GitHubRepoResponse> repos
    ) {
        public static RepoList from(List<String> repoNames) {
            List<GitHubRepoResponse> repos = repoNames.stream()
                                                      .map(GitHubRepoResponse::from)
                                                      .toList();
            return new RepoList(repos);
        }
    }
}
