package io.github.seonjiwon.code_combine.domain.solution.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommitDetail {
    private List<String> filePaths;
    private LocalDateTime commitDate;
}
