package io.github.seonjiwon.code_combine.domain.solution.code;

import io.github.seonjiwon.code_combine.global.code.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SolutionErrorCode implements BaseErrorCode {

    SOLUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SOLUTION404", "풀이가 존재하지 않습니다."),

    INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "SOLUTION400_01", "유효하지 않은 파일 경로입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}