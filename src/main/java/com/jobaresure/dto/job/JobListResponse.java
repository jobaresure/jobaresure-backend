package com.jobaresure.dto.job;

import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class JobListResponse {

    private String publicId;
    private String jobTitle;

    private String organizationName;

    private String location;

    private JobType jobType;
    private WorkMode workMode;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;

    private Boolean isFeatured;
    private Boolean isUrgent;

    private LocalDateTime createdAt;
}

