package io.github.seonjiwon.code_combine.domain.repo.repository;

import io.github.seonjiwon.code_combine.domain.repo.entity.Repo;
import io.github.seonjiwon.code_combine.domain.repo.entity.SyncStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoRepository extends JpaRepository<Repo, Long> {

    Optional<Repo> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    List<Repo> findBySyncStatusAndRetryCountLessThan(SyncStatus status, int retryCount);
}
