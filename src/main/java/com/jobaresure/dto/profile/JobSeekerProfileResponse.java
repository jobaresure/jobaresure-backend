package com.jobaresure.dto.profile;

import com.jobaresure.enums.NoticePeriod;
import com.jobaresure.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class JobSeekerProfileResponse {

    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePicture;
    private String headline;
    private String bio;
    private String currentLocation;
    private BigDecimal totalExperienceYears;
    private BigDecimal currentSalary;
    private BigDecimal expectedSalary;
    private String currency;
    private String resumeUrl;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private NoticePeriod noticePeriod;
    private Boolean isActivelyLooking;
    private Integer profileCompleted;
    private Boolean identityVerified;
    private Status status;

    private List<WorkExperienceDto> workExperiences;
    private List<EducationDto> educations;
    private List<ApplicantSkillDto> skills;
}
