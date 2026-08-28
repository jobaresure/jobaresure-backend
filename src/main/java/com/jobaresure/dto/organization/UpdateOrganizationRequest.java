package com.jobaresure.dto.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating an organization")
public class UpdateOrganizationRequest {

    @Schema(description = "Official company name", example = "Google Inc")
    private String companyName;

    @Schema(description = "Official company email", example = "hr@google.com")
    private String companyEmail;

    @Schema(description = "Company email domain", example = "google.com")
    private String emailDomain;

    @Schema(description = "Company website URL", example = "https://google.com")
    private String website;

    @Schema(description = "Industry type", example = "Technology")
    private String industry;

    @Schema(description = "Headquarters city and country", example = "California, USA")
    private String headquartersLocation;

    @Schema(description = "Year company was founded", example = "1998")
    private Integer foundedYear;

    @Schema(description = "Short company description", example = "Leading tech company...")
    private String description;

    @Schema(description = "Detailed about section")
    private String about;

    @Schema(description = "LinkedIn profile URL")
    private String linkedinUrl;

    @Schema(description = "Twitter profile URL")
    private String twitterUrl;

    @Schema(description = "Company status", example = "active")
    private String status;
}
