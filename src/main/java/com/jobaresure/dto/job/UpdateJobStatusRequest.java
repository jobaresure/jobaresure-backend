package com.jobaresure.dto.job;

import com.jobaresure.enums.job.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateJobStatusRequest {

    @NotNull
    private JobStatus status;
}
