package io.github.seonjiwon.code_combine.domain.solution.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCommit {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer commitCount;
}
