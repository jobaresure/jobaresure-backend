package com.jobaresure.repository;

import com.jobaresure.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Skill> search(String keyword, Pageable pageable);
}
