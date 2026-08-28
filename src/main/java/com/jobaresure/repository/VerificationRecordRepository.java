package com.jobaresure.repository;

import com.jobaresure.entity.VerificationRecord;
import com.jobaresure.enums.VerificationStatus;
import com.jobaresure.enums.VerificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, UUID> {

    List<VerificationRecord> findBySubmittedByUserIdOrderByCreatedAtDesc(UUID userId);

    List<VerificationRecord> findByTypeAndSubjectIdOrderByCreatedAtDesc(VerificationType type, UUID subjectId);

    Page<VerificationRecord> findByStatus(VerificationStatus status, Pageable pageable);

    boolean existsByTypeAndSubjectIdAndStatus(VerificationType type, UUID subjectId, VerificationStatus status);
}
