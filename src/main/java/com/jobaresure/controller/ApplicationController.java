package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.ApplicationResponse;
import com.jobaresure.dto.ApplyJobRequest;
import com.jobaresure.dto.UpdateApplicationStatusRequest;
import com.jobaresure.service.ApplicationService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications", description = "Job applications: apply, track, and review")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Apply to a job (job seeker)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @Valid @RequestBody ApplyJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted", applicationService.apply(request)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "List my applications (job seeker)")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> myApplications() {
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved",
                applicationService.getMyApplications()));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Withdraw one of my applications (job seeker)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> withdraw(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn",
                applicationService.withdraw(id)));
    }

    @GetMapping("/job/{jobPublicId}")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "List applications for one of my jobs (company)")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> applicationsForJob(
            @PathVariable String jobPublicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved",
                applicationService.getApplicationsForJob(jobPublicId, pageable)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Move an application through the hiring pipeline (company)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Application status updated",
                applicationService.updateStatus(id, request.getStatus())));
    }
}
