package com.jobaresure.dto.employer;

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
@Schema(description = "Response payload for employer details")
public class EmployerResponse {

    @Schema(description = "Unique identifier")
    private UUID id;

    @Schema(description = "User ID from authentication")
    private UUID userId;

    @Schema(description = "Organization ID")
    private UUID organizationId;

    @Schema(description = "Organization name")
    private String organizationName;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Email address")
    private String email;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "Profile picture URL")
    private String profilePicture;

    @Schema(description = "Job title/position")
    private String jobTitle;

    @Schema(description = "Department")
    private String department;

    @Schema(description = "Role in organization")
    private String role;

    @Schema(description = "Status")
    private String status;

    @Schema(description = "Is primary contact")
    private Boolean isPrimaryContact;

    @Schema(description = "Total jobs posted by this employer")
    private Long totalJobsPosted;

    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLogin;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAt;
}
