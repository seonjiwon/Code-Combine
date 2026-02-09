package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProblemSolution {
    private String sourceCode;
    private String language;
    private String username;
    private String avatarUrl;
}
