package com.jobaresure.dto.profile;

import com.jobaresure.enums.ProficiencyLevel;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ApplicantSkillDto {
    private UUID id;
    private UUID skillId;
    private String skillName;
    private String category;
    private ProficiencyLevel proficiency;
    private Integer yearsOfExperience;
}
