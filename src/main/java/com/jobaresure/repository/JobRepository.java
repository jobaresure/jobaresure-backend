package com.jobaresure.repository;

import com.jobaresure.entity.Job;
import com.jobaresure.enums.job.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    long countByOrganizationIdAndStatus(UUID organizationId, JobStatus status);

    long countByPostedById(UUID employerId);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Optional<Job> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);

    Page<Job> findAll(Pageable pageable);
}
