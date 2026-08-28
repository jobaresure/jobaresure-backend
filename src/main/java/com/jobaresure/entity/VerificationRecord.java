package com.jobaresure.entity;

import com.jobaresure.enums.DocumentType;
import com.jobaresure.enums.VerificationStatus;
import com.jobaresure.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A submitted verification request (identity for job seekers, legitimacy for
 * companies) reviewed by an admin. The document file itself is stored in object
 * storage (Supabase, Phase 5); only its URL + masked number are kept here.
 */
@Entity
@Table(name = "verification_records",
        indexes = {
                @Index(name = "idx_verif_subject", columnList = "type,subject_id"),
                @Index(name = "idx_verif_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationType type;

    /** Applicant.id (IDENTITY) or Organization.id (COMPANY). */
    @Column(name = "subject_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID subjectId;

    /** The User who submitted this request. */
    @Column(name = "submitted_by_user_id", columnDefinition = "CHAR(36)")
    private UUID submittedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentType documentType;

    @Column(name = "document_url", length = 1000)
    private String documentUrl;

    /** Stored masked (e.g. XXXXXX1234) — never the full government id. */
    @Column(name = "document_number_masked", length = 50)
    private String documentNumberMasked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;

    @Column(name = "reviewed_by_user_id", columnDefinition = "CHAR(36)")
    private UUID reviewedByUserId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
