package io.github.seonjiwon.code_combine.domain.problem.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolvedUser {
    private Long userId;
    private String username;
    private String avatarUrl;
}
