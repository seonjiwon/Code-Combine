package io.github.seonjiwon.code_combine.domain.solution.converter;

import io.github.seonjiwon.code_combine.domain.solution.dto.CommitInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.FileInfo;
import io.github.seonjiwon.code_combine.domain.solution.dto.SolutionData;
import java.util.List;

public class GitResponseConverter {

    public static CommitInfo toCommitInfo(String sha) {
        return CommitInfo.builder()
                         .sha(sha)
                         .build();
    }

    public static List<CommitInfo> toCommitList(List<String> commitShas) {
        return commitShas.stream()
                         .map(GitResponseConverter::toCommitInfo)
                         .toList();
    }

    public static FileInfo toFileInfo(String filePath) {
        return FileInfo.builder()
                       .filePath(filePath)
                       .build();
    }

    public static List<FileInfo> toFileInfos(List<String> filePaths) {
        return filePaths.stream()
                        .map(GitResponseConverter::toFileInfo)
                        .toList();
    }

    public static SolutionData toSolutionData(String sourceCode, String readMe) {
        return SolutionData.builder()
                           .sourceCode(sourceCode)
                           .readMe(readMe)
                           .build();
    }
}
