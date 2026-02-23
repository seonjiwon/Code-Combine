package io.github.seonjiwon.code_combine.domain.repo.controller;

import io.github.seonjiwon.code_combine.domain.repo.service.RepoService;
import io.github.seonjiwon.code_combine.domain.repo.dto.RepoRegisterRequest;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "레포 등록 API", description = "유저의 레포지토리를 등록합니다.")
public class RepoController {

    private final RepoService repoService;

    @PostMapping("/repo")
    @Operation(
        summary = "레포지토리 등록",
        description = "사용자의 GitHub 레포지토리를 등록하고 전체 커밋 초기 동기화를 시작합니다."
    )
    public CustomResponse<String> setRepository(
        @AuthenticationPrincipal Long userId,
        @Parameter(description = "사용자의 리포지토리 이름", example = "Java-Algorithm")
        @RequestBody RepoRegisterRequest repoRegisterRequest) {

        repoService.registerRepository(userId, repoRegisterRequest);
        return CustomResponse.onSuccess("레포지토리 등록 완료");
    }
}
