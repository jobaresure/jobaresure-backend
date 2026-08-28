package com.jobaresure.dto.employer;

import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating employer details")
public class UpdateEmployerRequest {

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "Job title/position")
    private String jobTitle;

    @Schema(description = "Department name")
    private String department;

    @Schema(description = "Role in the organization")
    private EmployerRole role;

    @Schema(description = "Status of the employer")
    private EmployerStatus status;

    @Schema(description = "Profile picture URL")
    private String profilePicture;
}
