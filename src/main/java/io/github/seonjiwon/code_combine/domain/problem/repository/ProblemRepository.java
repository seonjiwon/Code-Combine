package io.github.seonjiwon.code_combine.domain.problem.repository;

import io.github.seonjiwon.code_combine.domain.problem.entity.Problem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByProblemNumber(int problemNumber);


    @Query("SELECT p " +
        "FROM Problem p " +
        "ORDER BY p.id")
    List<Problem> findProblems(Pageable pageable);


    @Query("SELECT p " +
        "FROM Problem p " +
        "WHERE p.id > :cursor " +
        "ORDER BY p.id")
    List<Problem> findProblems(@Param("cursor") Long cursor, Pageable pageable);


    @Query("SELECT p "
        + "FROM Problem p "
        + "WHERE CAST(p.problemNumber AS string) LIKE :keyword% "
        + "ORDER BY p.problemNumber")
    List<Problem> findByProblemNumberStartingWith(@Param("keyword") String keyword);


    @Query("SELECT p "
        + "FROM Problem p "
        + "WHERE p.title LIKE :keyword% "
        + "ORDER BY p.problemNumber")
    List<Problem> findByTitleStartingWith(@Param("keyword") String keyword);
}