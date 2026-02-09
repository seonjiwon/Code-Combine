package io.github.seonjiwon.code_combine.domain.problem.service.query;

import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.ProblemSolveList;

public interface ProblemQueryService {

    ProblemSolveList getProblemSolveList(String cursor);
}
