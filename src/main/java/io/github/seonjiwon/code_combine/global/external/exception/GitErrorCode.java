package io.github.seonjiwon.code_combine.global.external.exception;

import io.github.seonjiwon.code_combine.global.code.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GitErrorCode implements BaseErrorCode {
    // GitHub API 호출 실패
    GITHUB_API_ERROR(HttpStatus.BAD_GATEWAY, "GIT-001", "GitHub API 호출에 실패했습니다."),

    // 인증 실패
    GITHUB_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GIT-002", "GitHub 인증에 실패했습니다."),

    // 리소스 없음
    GITHUB_RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GIT-003", "요청한 GitHub 리소스를 찾을 수 없습니다."),

    // Rate Limit 초과
    GITHUB_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "GIT-004", "GitHub API 요청 한도를 초과했습니다."),

    // 파일 파싱 실패
    GITHUB_FILE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GIT-005", "GitHub 파일 파싱에 실패했습니다."),

    // JSON 파싱 실패
    GITHUB_JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GIT-006", "GitHub API 응답 파싱에 실패했습니다."),

    // 잘못된 레포지토리
    GITHUB_INVALID_REPOSITORY(HttpStatus.BAD_REQUEST, "GIT-007", "유효하지 않은 레포지토리입니다."),

    // 잘못된 커밋 SHA
    GITHUB_INVALID_COMMIT_SHA(HttpStatus.BAD_REQUEST, "GIT-008", "유효하지 않은 커밋 SHA입니다."),

    // 파일 경로 오류
    GITHUB_INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "GIT-009", "유효하지 않은 파일 경로입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
