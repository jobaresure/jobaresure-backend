package com.jobaresure.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class WorkExperienceResponse {

    private UUID id;
    private String jobTitle;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
