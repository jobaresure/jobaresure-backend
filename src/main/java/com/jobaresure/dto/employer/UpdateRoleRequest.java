package com.jobaresure.dto.employer;

import com.jobaresure.enums.EmployerRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating employer role")
public class UpdateRoleRequest {

    @NotNull(message = "Role is required")
    @Schema(description = "New role", example = "admin")
    private EmployerRole role;
}
