package com.jobaresure.dto.profile;

import com.jobaresure.enums.CompanySize;
import com.jobaresure.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CompanyProfileResponse {

    private UUID id;
    private String publicId;
    private String companyName;
    private String companySlug;
    private String companyEmail;
    private String companyPhone;
    private String logoUrl;
    private String website;
    private String industry;
    private CompanySize companySize;
    private String description;
    private String about;
    private String headquartersLocation;
    private Integer foundedYear;
    private String linkedinUrl;
    private String twitterUrl;
    private Boolean isVerified;
    private Status status;

    /** The current user's employer record within this organization. */
    private EmployerProfileDto currentEmployer;
}
