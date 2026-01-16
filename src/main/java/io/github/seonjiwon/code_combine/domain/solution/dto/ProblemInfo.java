package io.github.seonjiwon.code_combine.domain.solution.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemInfo {

    private int problemNumber;
    private String title;
    private String language;

}
