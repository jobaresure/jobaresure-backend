package com.jobaresure.service.impl;

import com.jobaresure.dto.skill.CreateSkillRequest;
import com.jobaresure.dto.skill.SkillResponse;
import com.jobaresure.entity.Skill;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.repository.SkillRepository;
import com.jobaresure.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SkillResponse> list(String keyword, Pageable pageable) {
        Page<Skill> page = StringUtils.hasText(keyword)
                ? skillRepository.search(keyword.trim(), pageable)
                : skillRepository.findAll(pageable);
        return page.map(SkillResponse::from);
    }

    @Override
    @Transactional
    public SkillResponse create(CreateSkillRequest request) {
        String slug = slugify(request.getName());
        if (skillRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("A skill with this name already exists");
        }
        Skill skill = skillRepository.save(Skill.builder()
                .name(request.getName().trim())
                .slug(slug)
                .category(request.getCategory())
                .build());
        return SkillResponse.from(skill);
    }

    @Override
    @Transactional
    public Skill getOrCreateByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BadRequestException("Skill name is required");
        }
        String slug = slugify(name);
        return skillRepository.findBySlug(slug)
                .orElseGet(() -> skillRepository.save(Skill.builder()
                        .name(name.trim())
                        .slug(slug)
                        .build()));
    }

    private String slugify(String name) {
        String slug = name.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (slug.isBlank()) {
            throw new BadRequestException("Invalid skill name");
        }
        return slug;
    }
}
