package io.github.seonjiwon.code_combine.domain.user.repository;

import io.github.seonjiwon.code_combine.domain.user.entity.GitToken;
import io.github.seonjiwon.code_combine.domain.user.entity.TokenStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitTokenRepository extends JpaRepository<GitToken, Long> {
    Optional<GitToken> findByUserIdAndStatus(Long userId, TokenStatus status);
}
