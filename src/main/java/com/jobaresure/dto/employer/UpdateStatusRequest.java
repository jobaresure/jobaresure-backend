package com.jobaresure.dto.employer;

import com.jobaresure.enums.EmployerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating employer status")
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New status", example = "active")
    private EmployerStatus status;
}
