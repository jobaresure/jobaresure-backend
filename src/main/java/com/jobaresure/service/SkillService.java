package com.jobaresure.service;

import com.jobaresure.dto.skill.CreateSkillRequest;
import com.jobaresure.dto.skill.SkillResponse;
import com.jobaresure.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkillService {

    Page<SkillResponse> list(String keyword, Pageable pageable);

    SkillResponse create(CreateSkillRequest request);

    /** Resolve an existing catalog skill by free-text name, creating it if absent. */
    Skill getOrCreateByName(String name);
}
