package com.jobaresure.dto.profile;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EducationDto {
    private UUID id;
    private String degree;
    private String fieldOfStudy;
    private String institutionName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String grade;
    private String description;
}
