package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.skill.CreateSkillRequest;
import com.jobaresure.dto.skill.SkillResponse;
import com.jobaresure.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill catalog (public search; admin create)")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "List or search the skill catalog")
    public ResponseEntity<ApiResponse<Page<SkillResponse>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved", skillService.list(keyword, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a catalog skill (admin)")
    public ResponseEntity<ApiResponse<SkillResponse>> create(@Valid @RequestBody CreateSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill created", skillService.create(request)));
    }
}
