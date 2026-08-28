package com.jobaresure.repository;

import com.jobaresure.entity.ApplicantSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicantSkillRepository extends JpaRepository<ApplicantSkill, UUID> {

    List<ApplicantSkill> findByApplicantId(UUID applicantId);

    Optional<ApplicantSkill> findByApplicantIdAndSkillId(UUID applicantId, UUID skillId);

    boolean existsByApplicantIdAndSkillId(UUID applicantId, UUID skillId);

    Optional<ApplicantSkill> findByIdAndApplicantId(UUID id, UUID applicantId);
}
