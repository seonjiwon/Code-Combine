package io.github.seonjiwon.code_combine.domain.repo.controller;

import io.github.seonjiwon.code_combine.domain.repo.service.RepoService;
import io.github.seonjiwon.code_combine.domain.user.dto.UserRepoInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class RepoController {

    private final RepoService repoService;

    @PostMapping("/repo")
    public void setRepository(
        @AuthenticationPrincipal Long userId,
        @RequestBody UserRepoInfo userRepoInfo) {

        repoService.setRepository(userId, userRepoInfo);
    }
}
