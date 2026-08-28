package com.jobaresure.dto.job;

import com.jobaresure.enums.job.EducationLevel;
import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import com.jobaresure.enums.job.SeniorityLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateJobRequest {
    private String jobTitle;

    private JobType jobType;
    private WorkMode workMode;

    private String location;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private Boolean isSalaryVisible;

    private Integer experienceMin;
    private Integer experienceMax;

    private LocalDate applicationDeadline;

    private String jobDescription;
    private String responsibilities;
    private String requirements;
    private String preferredQualifications;
    private String benefits;
    private String applicationInstructions;

    private Integer numberOfOpenings;
    private String department;

    private SeniorityLevel seniorityLevel;
    private EducationLevel educationLevel;

    private Boolean isFeatured;
    private Boolean isUrgent;
}
