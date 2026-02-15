package io.github.seonjiwon.code_combine.domain.repo.code;

import io.github.seonjiwon.code_combine.global.code.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RepoErrorCode implements BaseErrorCode {

    REPO_NOT_FOUND(HttpStatus.NOT_FOUND, "REPO_001", "리포지토리를 찾을 수 없습니다."),
    REPO_ALREADY_EXISTS(HttpStatus.CONFLICT, "REPO_002", "이미 등록된 리포지토리입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
