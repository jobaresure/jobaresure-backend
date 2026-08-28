package com.jobaresure.dto;

import com.jobaresure.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotNull
    private ApplicationStatus status;
}
