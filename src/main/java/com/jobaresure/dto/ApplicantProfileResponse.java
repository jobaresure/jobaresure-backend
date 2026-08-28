package com.jobaresure.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ApplicantProfileResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String headline;
    private String currentLocation;
    private Double totalExperienceYears;

    private List<EducationResponse> educations;
    private List<WorkExperienceResponse> workExperiences;
}
