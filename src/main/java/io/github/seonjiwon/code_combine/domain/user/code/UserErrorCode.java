package io.github.seonjiwon.code_combine.domain.user.code;

import io.github.seonjiwon.code_combine.global.code.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "존재하지 않는 사용자입니다."),

    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409_01", "이미 존재하는 이메일입니다."),

    ACTIVE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_02", "활성화된 GitHub 토큰이 없습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}