package io.github.seonjiwon.code_combine.domain.solution.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolutionData{
    private String sourceCode;
    private String readMe;
}
