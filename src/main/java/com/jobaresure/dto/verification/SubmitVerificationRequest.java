package com.jobaresure.dto.verification;

import com.jobaresure.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Shared shape for submitting an identity (job seeker) or company verification.
 * The document file is uploaded to object storage first (Phase 5); its URL is
 * passed here. The full document number is masked server-side before storage.
 */
@Data
public class SubmitVerificationRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    /** Full government/business id number — stored masked, never in the clear. */
    private String documentNumber;
}
