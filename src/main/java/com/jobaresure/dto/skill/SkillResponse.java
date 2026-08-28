package com.jobaresure.dto.skill;

import com.jobaresure.entity.Skill;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SkillResponse {
    private UUID id;
    private String name;
    private String slug;
    private String category;

    public static SkillResponse from(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .slug(skill.getSlug())
                .category(skill.getCategory())
                .build();
    }
}
