package com.jobaresure.dto.employer;

import com.jobaresure.enums.EmployerRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating a new employer")
public class CreateEmployerRequest {

    @Schema(description = "First name of the employer")
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Schema(description = "Last name of the employer")
    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Schema(description = "Work email address")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Phone number")
    @Size(max = 20)
    private String phone;

    @Schema(description = "Job title/position", example = "HR Manager")
    @Size(max = 150)
    private String jobTitle;

    @Schema(description = "Department name", example = "HR")
    @Size(max = 100)
    private String department;

    @Schema(description = "Role in the organization", example = "recruiter")
    private EmployerRole role;

    @Schema(description = "Profile picture URL")
    @Size(max = 500)
    private String profilePicture;
}
