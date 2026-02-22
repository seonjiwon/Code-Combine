package io.github.seonjiwon.code_combine.domain.problem.dto;

public record UserSolverProjection(
    Long problemId,
    Long userId,
    String username,
    String avatarUrl
) {

}
