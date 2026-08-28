package com.jobaresure.dto.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new organization")
public class CreateOrganizationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Schema(description = "Official company name", example = "Google Inc")
    private String companyName;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Official company email", example = "hr@google.com")
    private String companyEmail;

    @Size(max = 100, message = "Email domain must not exceed 100 characters")
    @Schema(description = "Company email domain", example = "google.com")
    private String emailDomain;

    @Size(max = 100, message = "Email domain must not exceed 100 characters")
    @Schema(description = "Company email domain", example = "google.com")
    private String website;

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    @Schema(description = "Industry type", example = "Technology")
    private String industry;

    @Size(max = 255, message = "Headquarters location must not exceed 255 characters")
    @Schema(description = "Headquarters city and country", example = "California, USA")
    private String headquartersLocation;

    @Min(value = 1800, message = "Founded year must be after 1800")
    @Max(value = 2030, message = "Founded year must be before 2030")
    @Schema(description = "Year company was founded", example = "1998")
    private Integer foundedYear;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Short company description", example = "Leading tech company...")
    private String description;

    @Size(max = 255, message = "LinkedIn URL must not exceed 255 characters")
    private String linkedinUrl;

    @Size(max = 255, message = "Twitter URL must not exceed 255 characters")
    private String twitterUrl;
}
