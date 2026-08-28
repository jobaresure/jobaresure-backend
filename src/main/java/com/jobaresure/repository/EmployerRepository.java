package com.jobaresure.repository;

import com.jobaresure.entity.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface EmployerRepository extends JpaRepository<Employer, UUID> {

    long countByOrganizationId(UUID organizationId);

    boolean existsByEmail(String email);

    Optional<Employer> findByUserId(UUID userId);

    Page<Employer> findByOrganizationId(UUID organizationId, Pageable pageable);


}
