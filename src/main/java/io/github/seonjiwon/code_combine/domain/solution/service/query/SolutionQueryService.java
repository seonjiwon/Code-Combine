package io.github.seonjiwon.code_combine.domain.solution.service.query;

import io.github.seonjiwon.code_combine.domain.solution.code.SolutionErrorCode;
import io.github.seonjiwon.code_combine.domain.solution.domain.Solution;
import io.github.seonjiwon.code_combine.domain.solution.dto.ProblemSolution;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionResponse;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionResponse.Submission;
import io.github.seonjiwon.code_combine.domain.solution.repository.SolutionRepository;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionQueryService {

    private final SolutionRepository solutionRepository;

    public Solution getById(Long solutionId) {
        return solutionRepository.findById(solutionId)
                                 .orElseThrow(() -> new CustomException(SolutionErrorCode.SOLUTION_NOT_FOUND));
    }

    public SolutionResponse.Detail getDetailSolution(Long problemId) {
        List<ProblemSolution> solutions = solutionRepository.findAllSolutionsByProblemId(problemId);

        List<Submission> submissions = solutions.stream()
                                                .map(solution ->
                                                    Submission.builder()
                                                              .solutionId(solution.getSolutionId())
                                                              .username(solution.getUsername())
                                                              .language(solution.getLanguage())
                                                              .submissionCode(
                                                                  solution.getSourceCode())
                                                              .solveExplain(null)
                                                              .build())
                                                .toList();
        return SolutionResponse.Detail.builder()
                                      .submissions(submissions)
                                      .build();
    }
}
