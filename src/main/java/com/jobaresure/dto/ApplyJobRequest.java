package com.jobaresure.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplyJobRequest {

    @NotNull
    private UUID jobId;

    /** Optional — falls back to the applicant's profile resume when omitted. */
    private String resumeUrl;

    private String coverLetter;
}
