package io.github.seonjiwon.code_combine.domain.user.service;

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

    /**
     * OAuth2 로그인 시 사용자 조회 또는 생성
     * gitId 조회시 존재하지 않으면 새로 생성
     */
    public User findOrCreateUser(OAuth2UserInfo userInfo) {
        return userRepository.findByGitId(userInfo.getId())
            .orElseGet(() -> createUser(userInfo));
    }

    /**
     * 새로운 사용자 생성
     */
    private User createUser(OAuth2UserInfo userInfo) {
        User newUser = User.builder()
            .username(userInfo.getUsername())
            .gitId(userInfo.getId())
            .avatarUrl(userInfo.getAvatarUrl())
            .build();

        User savedUser = userRepository.save(newUser);
        log.info("새로운 사용자 생성: gitId={}, username={}", savedUser.getGitId(), savedUser.getUsername());

        return savedUser;
    }

    /**
     * 로그인 성공 후 사용자 정보 조회
     */
    public LoginSuccessResponse getLoginSuccessUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return LoginSuccessResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }
}
