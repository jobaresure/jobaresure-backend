package com.jobaresure.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkExperienceRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
}
