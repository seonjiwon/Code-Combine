package io.github.seonjiwon.code_combine.domain.problem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSolver {
    private Long problemId;
    private Long userId;
    private String username;
    private String avatarUrl;
}
