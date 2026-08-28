package com.jobaresure.dto.job;

import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateJobRequest {

    @NotBlank
    private String jobTitle;

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID postedBy;

    @NotNull
    private JobType jobType;

    @NotNull
    private WorkMode workMode;

    private String location;

    @NotBlank
    private String jobDescription;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;

    private Integer experienceMin;
    private Integer experienceMax;

    private LocalDate applicationDeadline;

}
