package com.jobaresure.dto.verification;

import com.jobaresure.entity.VerificationRecord;
import com.jobaresure.enums.DocumentType;
import com.jobaresure.enums.VerificationStatus;
import com.jobaresure.enums.VerificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VerificationResponse {

    private UUID id;
    private VerificationType type;
    private UUID subjectId;
    private DocumentType documentType;
    private String documentUrl;
    private String documentNumberMasked;
    private VerificationStatus status;
    private String reviewNotes;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    public static VerificationResponse from(VerificationRecord r) {
        return VerificationResponse.builder()
                .id(r.getId())
                .type(r.getType())
                .subjectId(r.getSubjectId())
                .documentType(r.getDocumentType())
                .documentUrl(r.getDocumentUrl())
                .documentNumberMasked(r.getDocumentNumberMasked())
                .status(r.getStatus())
                .reviewNotes(r.getReviewNotes())
                .submittedAt(r.getCreatedAt())
                .reviewedAt(r.getReviewedAt())
                .build();
    }
}
