package io.github.seonjiwon.code_combine.domain.repo.repository;

import io.github.seonjiwon.code_combine.domain.repo.domain.Repo;
import io.github.seonjiwon.code_combine.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoRepository extends JpaRepository<Repo, Long> {

    Optional<Repo> findByUserId(Long userId);

    Optional<Repo> findByUser(User user);

    boolean existsByUserId(Long userId);
}
