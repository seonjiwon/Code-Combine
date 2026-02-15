package io.github.seonjiwon.code_combine.domain.user.service;

import io.github.seonjiwon.code_combine.domain.repo.code.RepoErrorCode;
import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.repo.repository.RepoRepository;
import io.github.seonjiwon.code_combine.domain.solution.service.command.InitialSyncService;
import io.github.seonjiwon.code_combine.domain.user.code.UserErrorCode;
import io.github.seonjiwon.code_combine.domain.user.dto.LoginSuccessResponse;
import io.github.seonjiwon.code_combine.domain.user.dto.OAuth2UserInfo;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
import io.github.seonjiwon.code_combine.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RepoRepository repoRepository;
    private final InitialSyncService initialSyncService;

    // OAuth2 로그인 시 사용자 조회 또는 생성
    public User findOrCreateUser(OAuth2UserInfo userInfo) {
        // 사용자가 존재하면 기존 사용자 반환, 아니면 생성
        return userRepository.findByEmail(userInfo.getEmail())
                             .orElseGet(() -> createUser(userInfo));
    }

    private User createUser(OAuth2UserInfo userInfo) {
        User newUser = User.builder()
                         .username(userInfo.getUsername())
                         .email(userInfo.getEmail())
                         .avatarUrl(userInfo.getAvatarUrl())
                         .build();

        User savedUser = userRepository.save(newUser);
        log.info("새로운 사용자 생성: {}", savedUser.getEmail());

        return savedUser;
    }

    public LoginSuccessResponse getLoginSuccessUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(
                                      () -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return LoginSuccessResponse.builder()
                                   .userId(user.getId())
                                   .username(user.getUsername())
                                   .avatarUrl(user.getAvatarUrl())
                                   .build();
    }

    public void performanceInitialSync(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (user.getLastSyncAt() != null) {
            log.info("이미 동기화된 사용자입니다.");
            return;
        }

        // Repo 조회
        Repo repo = repoRepository.findByUser(user)
                                  .orElseThrow(() -> new CustomException(RepoErrorCode.REPO_NOT_FOUND));

        initialSyncService.syncAllCommits(user, user.getUsername(), repo.getName());

        log.info("최초 동기화 완료: userId={}", userId);
    }
}
