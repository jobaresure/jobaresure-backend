package com.jobaresure.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobDetailResponse {

    private UUID id;
    private String jobTitle;
    private String companyName;
    private String jobDescription;
    private String responsibilities;
    private String requirements;
    private String benefits;
    private String location;
    private String jobType;
    private String workMode;
    private Double salaryMin;
    private Double salaryMax;
    private String status;
    private LocalDateTime createdAt;
}
