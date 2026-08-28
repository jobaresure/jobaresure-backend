package com.jobaresure.repository;

import com.jobaresure.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, UUID> {

    Optional<Applicant> findByUserId(UUID userId);

    Optional<Applicant> findByEmail(String email);

    boolean existsByEmail(String email);
}
