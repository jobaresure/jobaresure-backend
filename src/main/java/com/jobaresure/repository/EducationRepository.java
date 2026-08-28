package com.jobaresure.repository;

import com.jobaresure.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findByApplicantIdOrderByStartDateDesc(UUID applicantId);

    Optional<Education> findByIdAndApplicantId(UUID id, UUID applicantId);
}
