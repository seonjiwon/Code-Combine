package io.github.seonjiwon.code_combine.domain.problem.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemSolveList {
    private List<SolveInfo> problemList;
    String cursor;
}
