package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.profile.*;
import com.jobaresure.service.JobSeekerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('JOB_SEEKER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Job Seeker Profile", description = "Self-service profile, experience, education & skills")
public class ProfileController {

    private final JobSeekerProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Get my full job seeker profile")
    public ResponseEntity<ApiResponse<JobSeekerProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profileService.getMyProfile()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile (partial)")
    public ResponseEntity<ApiResponse<JobSeekerProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateJobSeekerProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profileService.updateMyProfile(request)));
    }

    // ---- Work experience ----

    @PostMapping("/me/experiences")
    @Operation(summary = "Add a work experience")
    public ResponseEntity<ApiResponse<WorkExperienceDto>> addExperience(
            @Valid @RequestBody WorkExperienceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Experience added", profileService.addExperience(request)));
    }

    @PutMapping("/me/experiences/{id}")
    @Operation(summary = "Update a work experience")
    public ResponseEntity<ApiResponse<WorkExperienceDto>> updateExperience(
            @PathVariable UUID id, @Valid @RequestBody WorkExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience updated",
                profileService.updateExperience(id, request)));
    }

    @DeleteMapping("/me/experiences/{id}")
    @Operation(summary = "Delete a work experience")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(@PathVariable UUID id) {
        profileService.deleteExperience(id);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted"));
    }

    // ---- Education ----

    @PostMapping("/me/educations")
    @Operation(summary = "Add an education entry")
    public ResponseEntity<ApiResponse<EducationDto>> addEducation(
            @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Education added", profileService.addEducation(request)));
    }

    @PutMapping("/me/educations/{id}")
    @Operation(summary = "Update an education entry")
    public ResponseEntity<ApiResponse<EducationDto>> updateEducation(
            @PathVariable UUID id, @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education updated",
                profileService.updateEducation(id, request)));
    }

    @DeleteMapping("/me/educations/{id}")
    @Operation(summary = "Delete an education entry")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@PathVariable UUID id) {
        profileService.deleteEducation(id);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    // ---- Skills ----

    @PostMapping("/me/skills")
    @Operation(summary = "Add a skill (by id or name)")
    public ResponseEntity<ApiResponse<ApplicantSkillDto>> addSkill(
            @Valid @RequestBody AddApplicantSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added", profileService.addSkill(request)));
    }

    @DeleteMapping("/me/skills/{applicantSkillId}")
    @Operation(summary = "Remove a skill from my profile")
    public ResponseEntity<ApiResponse<Void>> removeSkill(@PathVariable UUID applicantSkillId) {
        profileService.removeSkill(applicantSkillId);
        return ResponseEntity.ok(ApiResponse.success("Skill removed"));
    }
}
