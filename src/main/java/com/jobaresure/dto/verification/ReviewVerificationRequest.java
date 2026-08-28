package com.jobaresure.dto.verification;

import com.jobaresure.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewVerificationRequest {

    /** Must be APPROVED, REJECTED, or RESUBMIT_REQUIRED. */
    @NotNull(message = "Decision is required")
    private VerificationStatus decision;

    private String reviewNotes;
}
