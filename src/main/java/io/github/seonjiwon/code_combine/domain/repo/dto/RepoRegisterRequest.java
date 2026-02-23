package io.github.seonjiwon.code_combine.domain.repo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepoRegisterRequest {
    @NotBlank(message = "레포지토리 이름은 필수입니다.")
    private final String name;
}
