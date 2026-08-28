package com.jobaresure.repository;

import com.jobaresure.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, UUID> {

    List<WorkExperience> findByApplicantIdOrderByStartDateDesc(UUID applicantId);

    Optional<WorkExperience> findByIdAndApplicantId(UUID id, UUID applicantId);
}
