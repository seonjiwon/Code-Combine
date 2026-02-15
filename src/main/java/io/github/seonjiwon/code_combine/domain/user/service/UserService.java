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
     */
    public User findOrCreateUser(OAuth2UserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
            .orElseGet(() -> createUser(userInfo));
    }

    /**
     * 새로운 사용자 생성
     */
    private User createUser(OAuth2UserInfo userInfo) {
        User newUser = User.builder()
            .username(userInfo.getUsername())
            .email(userInfo.getEmail())
            .avatarUrl(userInfo.getAvatarUrl())
            .build();

        User savedUser = userRepository.save(newUser);
        log.info("새로운 사용자 생성: email={}, username={}", savedUser.getEmail(), savedUser.getUsername());

        return savedUser;
    }

    /**
     * 로그인 성공 후 사용자 정보 조회
     */
    public LoginSuccessResponse getLoginSuccessUserInfo(Long userId) {
        User user = findUserById(userId);

        return LoginSuccessResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }

    /**
     * ID로 사용자 조회
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }
}
