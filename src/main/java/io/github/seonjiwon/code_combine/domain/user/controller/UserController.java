package io.github.seonjiwon.code_combine.domain.user.controller;

import io.github.seonjiwon.code_combine.domain.user.dto.LoginSuccessResponse;
import io.github.seonjiwon.code_combine.domain.user.service.UserService;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/auth/me")
    public CustomResponse<LoginSuccessResponse> getLoginSuccessUserInfo(
        @AuthenticationPrincipal Long userId) {

        LoginSuccessResponse loginSuccessUserInfo = userService.getLoginSuccessUserInfo(userId);
        return CustomResponse.onSuccess(loginSuccessUserInfo);
    }
}
