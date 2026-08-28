package com.jobaresure.dto.job;

import com.jobaresure.enums.job.EducationLevel;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import com.jobaresure.enums.job.SeniorityLevel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchCriteria {


    private String keyword;
    private String location;

    private JobType jobType;
    private WorkMode workMode;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;

    private Integer experienceMin;
    private Integer experienceMax;

    private SeniorityLevel seniorityLevel;
    private EducationLevel educationLevel;

    private Boolean isFeatured;
    private Boolean isUrgent;

    private String organizationPublicId;
    private JobStatus status;
}
