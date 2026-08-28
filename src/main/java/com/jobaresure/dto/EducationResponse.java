package com.jobaresure.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EducationResponse {

    private UUID id;
    private String degree;
    private String fieldOfStudy;
    private String institutionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
