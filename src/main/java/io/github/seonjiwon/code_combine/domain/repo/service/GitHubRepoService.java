package io.github.seonjiwon.code_combine.domain.repo.service;

import io.github.seonjiwon.code_combine.domain.repo.dto.GitHubRepoResponse.RepoList;
import io.github.seonjiwon.code_combine.domain.user.service.TokenService;
import io.github.seonjiwon.code_combine.global.infra.github.GitHubFetcher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubRepoService {

    private final TokenService tokenService;
    private final GitHubFetcher fetcher;

    /**
     * 사용자 전체 Repository 목록 조회
     */
    public RepoList getGithubRepositories(Long userId) {
        String token = tokenService.getActiveToken(userId);
        List<String> repoNames = fetcher.fetchUserRepos(token);
        return RepoList.from(repoNames);
    }
}
