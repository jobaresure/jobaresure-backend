package com.jobaresure.dto;

import com.jobaresure.entity.Application;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {

    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String applicantName;
    private String status;
    private LocalDateTime appliedAt;

    public static ApplicationResponse from(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getJobTitle())
                .applicantName(application.getApplicant().getFirstName()
                        + " " + application.getApplicant().getLastName())
                .status(application.getStatus().name())
                .appliedAt(application.getCreatedAt())
                .build();
    }
}
