package com.jobaresure.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationRequest {

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;

    @NotBlank(message = "Institution name is required")
    private String institutionName;

    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String grade;
    private String description;
}
