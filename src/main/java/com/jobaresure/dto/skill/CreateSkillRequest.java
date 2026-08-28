package com.jobaresure.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 120)
    private String name;

    @Size(max = 80)
    private String category;
}
