package com.jobaresure.dto.organization;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for organization details")
public class OrganizationResponse {

    @Schema(description = "Unique identifier of the organization", example = "a2f6c9e3-91c7-4d5b-9d5b-4c3b8a9d2a1f")
    private UUID id;

    @Schema(description = "Official company name", example = "Google Inc")
    private String companyName;

    @Schema(description = "URL-friendly company name", example = "google-inc")
    private String companySlug;

    @Schema(description = "Company email domain", example = "google.com")
    private String emailDomain;

    @Schema(description = "Official company email", example = "hr@google.com")
    private String companyEmail;

    @Schema(description = "Company phone number", example = "+1-800-123-4567")
    private String companyPhone;

    @Schema(description = "URL to company logo", example = "https://logo.url/logo.png")
    private String logoUrl;

    @Schema(description = "Company website", example = "https://google.com")
    private String website;

    @Schema(description = "Industry type", example = "Technology")
    private String industry;

    @Schema(description = "Company size range", example = "500+")
    private String companySize;

    @Schema(description = "Short company description")
    private String description;

    @Schema(description = "Detailed about section")
    private String about;

    @Schema(description = "Headquarters location", example = "California, USA")
    private String headquartersLocation;

    @Schema(description = "Year company was founded", example = "1998")
    private Integer foundedYear;

    @Schema(description = "LinkedIn profile URL")
    private String linkedinUrl;

    @Schema(description = "Twitter profile URL")
    private String twitterUrl;

    @Schema(description = "Whether organization is verified by admin")
    private Boolean isVerified;

    @Schema(description = "Organization status", example = "active")
    private String status;

    @Schema(description = "Total number of active jobs")
    private Long activeJobsCount;

    @Schema(description = "Total number of employers")
    private Long employersCount;

    @Schema(description = "Timestamp when organization was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when organization was last updated")
    private LocalDateTime updatedAt;
}
