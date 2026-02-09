package io.github.seonjiwon.code_combine.domain.problem.controller;

import io.github.seonjiwon.code_combine.domain.problem.dto.ProblemsResponse.ProblemSolveList;
import io.github.seonjiwon.code_combine.domain.problem.service.query.v0.ProblemQueryServiceV0;
import io.github.seonjiwon.code_combine.global.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemQueryServiceV0 problemService;

    @GetMapping("/problems")
    public CustomResponse<ProblemSolveList> getProblemList(
        @RequestParam(value = "cursor") String cursor
    ) {
        ProblemSolveList problemSolveList = problemService.getProblemSolveList(cursor);
        return CustomResponse.onSuccess(problemSolveList);
    }
}
