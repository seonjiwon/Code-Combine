package io.github.seonjiwon.code_combine.domain.problem.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolveInfo {
    private int problemNumber;
    private String problemName;
    private int solvedUserCount;
    private List<SolvedUser> solvedUsers;
}
