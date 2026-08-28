package com.jobaresure.dto.profile;

import com.jobaresure.enums.NoticePeriod;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/** Partial update of a job seeker's core profile. Null fields are left unchanged. */
@Data
public class UpdateJobSeekerProfileRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String headline;

    @Size(max = 500)
    private String bio;

    @Size(max = 255)
    private String currentLocation;

    private BigDecimal totalExperienceYears;
    private BigDecimal currentSalary;
    private BigDecimal expectedSalary;
    private String currency;

    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;

    private NoticePeriod noticePeriod;
    private Boolean isActivelyLooking;
}
