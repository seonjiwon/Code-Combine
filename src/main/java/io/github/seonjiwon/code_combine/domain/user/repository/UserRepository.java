package io.github.seonjiwon.code_combine.domain.user.repository;

import io.github.seonjiwon.code_combine.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
