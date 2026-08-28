package com.jobaresure.repository;

import com.jobaresure.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    // Check exists
    boolean existsByCompanyName(String companyName);

    boolean existsByCompanyEmail(String companyEmail);

    // Find by name
    Optional<Organization> findByCompanyName(String companyName);

//    // Find with stats (avoid N+1 problem)
//    @Query("SELECT o FROM Organization o " +
//            "LEFT JOIN FETCH o.employers " +
//            "LEFT JOIN FETCH o.jobs " +
//            "WHERE o.id = :id")
//    Optional<Organization> findByIdWithStats(UUID id);
//
//    // Find all with stats
//    @Query("SELECT DISTINCT o FROM Organization o " +
//            "LEFT JOIN FETCH o.employers " +
//            "LEFT JOIN FETCH o.jobs")
//    Page<Organization> findAllWithStats(Pageable pageable);

    Optional<Organization> findById(UUID id);
    Page<Organization> findAll(Pageable pageable);

    // Find by slug
    Optional<Organization> findByCompanySlug(String slug);

    // Search by company name
    @Query("SELECT o FROM Organization o WHERE LOWER(o.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Organization> searchByName(String keyword, Pageable pageable);
}

