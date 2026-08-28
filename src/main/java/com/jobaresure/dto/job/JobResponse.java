package com.jobaresure.dto.job;


import com.jobaresure.enums.job.EducationLevel;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import com.jobaresure.enums.job.SeniorityLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {

    private String publicId;

    private String jobTitle;

    private String organizationPublicId;
    private String organizationName;

    private String postedByPublicId;
    private String postedByName;

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

    private JobStatus status;
    private Boolean isFeatured;
    private Boolean isUrgent;

    private Integer totalApplications;
    private Integer viewsCount;

    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
