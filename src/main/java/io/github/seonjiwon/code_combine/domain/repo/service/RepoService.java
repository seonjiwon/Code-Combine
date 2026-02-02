package io.github.seonjiwon.code_combine.domain.repo.service;

import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.repo.repository.RepoRepository;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.dto.UserRepoInfo;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RepoService {

    private final RepoRepository repoRepository;
    private final UserRepository userRepository;

    public void setRepository(Long userId, UserRepoInfo userRepoInfo) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(
                                      () -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (repoRepository.existsByUserId(userId)) {
            log.info("이미 Repository 가 등록되어 있습니다.");
            return;
        }

        Repo repo = Repo.builder()
                         .user(user)
                         .name(userRepoInfo.getName())
                         .build();
        repoRepository.save(repo);
    }
}
