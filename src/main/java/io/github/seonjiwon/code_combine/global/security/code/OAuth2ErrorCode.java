package io.github.seonjiwon.code_combine.global.security.code;

import io.github.seonjiwon.code_combine.global.code.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum OAuth2ErrorCode implements BaseErrorCode {
    GET_TOKEN_ERROR(HttpStatus.NOT_FOUND, "TOKEN", "OAuth2 Access Token을 가져올 수 없습니다."),



    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
