package io.github.seonjiwon.code_combine.domain.user.service;

import io.github.seonjiwon.code_combine.domain.user.dto.OAuth2UserInfo;
import io.github.seonjiwon.code_combine.domain.user.entity.User;
import io.github.seonjiwon.code_combine.domain.user.repository.UserRepository;
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
}
