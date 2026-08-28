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
@Schema(description = "Response payload for organization list item (minimal data)")
public class OrganizationListResponse {

    @Schema(description = "Unique identifier", example = "a2f6c9e3-91c7-4d5b-9d5b-4c3b8a9d2a1f")
    private UUID id;

    @Schema(description = "Company name", example = "Google Inc")
    private String companyName;

    @Schema(description = "Company logo URL")
    private String logoUrl;

    @Schema(description = "Industry", example = "Technology")
    private String industry;

    @Schema(description = "Headquarters location", example = "California, USA")
    private String headquartersLocation;

    @Schema(description = "Whether verified", example = "true")
    private Boolean isVerified;

    @Schema(description = "Total number of employers", example = "5")
    private Long employersCount;

    @Schema(description = "Total number of active jobs", example = "10")
    private Long activeJobsCount;


    @Schema(description = "Status", example = "active")
    private String status;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;
}