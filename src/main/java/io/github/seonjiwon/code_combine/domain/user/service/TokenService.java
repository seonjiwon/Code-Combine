package io.github.seonjiwon.code_combine.domain.user.service;

import io.github.seonjiwon.code_combine.domain.user.entity.GitToken;
import io.github.seonjiwon.code_combine.domain.user.entity.TokenStatus;
import io.github.seonjiwon.code_combine.domain.user.entity.User;
import io.github.seonjiwon.code_combine.domain.user.repository.GitTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final GitTokenRepository gitTokenRepository;

    // OAuth2 토큰 저장 또는 갱싱
    public void saveOrUpdateToken(User user, String plainToken) {
        // 1. 활성화된 토큰을 조회해서 토큰이 있으면 이를 비활성화
        gitTokenRepository.findByUserIdAndStatus(user.getId(), TokenStatus.ACTIVATED)
            .ifPresent(GitToken::deactivate);

        // 2. 새로운 토큰은 ACTIVATE 상태로 만듬
        GitToken newToken = GitToken.builder()
                                 .user(user)
                                 .token(plainToken)
                                 .issuedAt(LocalDateTime.now())
                                 .expiresAt(calculateExpiration())
                                 .status(TokenStatus.ACTIVATED)
                                 .build();
        gitTokenRepository.save(newToken);
        log.info("사용자 {} 토큰 저장 완료", user.getEmail());
    }

    private LocalDateTime calculateExpiration() {
        return LocalDateTime.now().plusDays(30);
    }
}
