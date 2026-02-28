package io.github.seonjiwon.code_combine.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginSuccessResponse {

    private Long userId;
    private String username;
    private String avatarUrl;

    private boolean hasRepo;
}
