package com.jobaresure.dto.profile;

import com.jobaresure.enums.ProficiencyLevel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Adds a skill to the current job seeker. Either reference an existing catalog
 * skill by id, or pass a free-text name which will be resolved/created in the
 * catalog automatically.
 */
@Data
public class AddApplicantSkillRequest {

    /** Optional: id of an existing catalog skill. */
    private String skillId;

    /** Required if skillId is not provided: the skill name (will be created if new). */
    private String skillName;

    private ProficiencyLevel proficiency;
    private Integer yearsOfExperience;

    public boolean hasSkillId() {
        return skillId != null && !skillId.isBlank();
    }
}
