package com.jobaresure.repository;

import com.jobaresure.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByJobIdAndApplicantId(UUID jobId, UUID applicantId);

    List<Application> findByApplicantIdOrderByCreatedAtDesc(UUID applicantId);

    Optional<Application> findByIdAndApplicantId(UUID id, UUID applicantId);

    Page<Application> findByJobId(UUID jobId, Pageable pageable);
}
