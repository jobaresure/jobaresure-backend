package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.employer.*;
import com.jobaresure.service.EmployerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Employer Management", description = "APIs for managing employers/recruiters")
public class EmployerController {

    private final EmployerService employerService;

    // ==================== CREATE ====================

    @PostMapping("/organizations/{orgId}/employers")
    @Operation(
            summary = "Create new employer",
            description = "Adds a new employer/recruiter under an organization"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> createEmployer(
            @Parameter(description = "Organization UUID")
            @PathVariable UUID orgId,

            @Valid @RequestBody CreateEmployerRequest request) {

        EmployerResponse response = employerService.createEmployer(orgId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employer created successfully", response));
    }

    // ==================== GET SINGLE ====================

    @GetMapping("/employers/{id}")
    @Operation(
            summary = "Get employer by ID",
            description = "Retrieves detailed information of a specific employer"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> getEmployerById(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id) {

        EmployerResponse response = employerService.getEmployerById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Employer retrieved successfully", response));
    }

    // ==================== GET ALL (PAGINATED) ====================

    @GetMapping("/employers")
    @Operation(
            summary = "Get all employers",
            description = "Retrieves paginated list of all employers"
    )
    public ResponseEntity<ApiResponse<Page<EmployerListResponse>>> getAllEmployers(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        Page<EmployerListResponse> employers = employerService.getAllEmployers(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Employers retrieved successfully", employers));
    }

    // ==================== GET BY ORGANIZATION ====================

    @GetMapping("/organizations/{orgId}/employers")
    @Operation(
            summary = "Get employers by organization",
            description = "Retrieves all employers under a specific organization"
    )
    public ResponseEntity<ApiResponse<Page<EmployerListResponse>>> getEmployersByOrganization(
            @Parameter(description = "Organization UUID")
            @PathVariable UUID orgId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<EmployerListResponse> employers =
                employerService.getEmployersByOrganization(orgId, pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Employers retrieved successfully", employers));
    }

    // ==================== UPDATE ====================

    @PutMapping("/employers/{id}")
    @Operation(
            summary = "Update employer",
            description = "Updates an existing employer's information"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> updateEmployer(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id,

            @Valid @RequestBody UpdateEmployerRequest request) {

        EmployerResponse response = employerService.updateEmployer(id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Employer updated successfully", response));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/employers/{id}")
    @Operation(
            summary = "Delete employer",
            description = "Permanently deletes an employer from the system"
    )
    public ResponseEntity<ApiResponse<Void>> deleteEmployer(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id) {

        employerService.deleteEmployer(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Employer deleted successfully"));
    }

    // ==================== UPDATE STATUS ====================

    @PatchMapping("/employers/{id}/status")
    @Operation(
            summary = "Update employer status",
            description = "Changes the status of an employer"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> updateStatus(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id,

            @Valid @RequestBody UpdateStatusRequest request) {

        EmployerResponse response = employerService.updateStatus(id, request.getStatus());

        return ResponseEntity
                .ok(ApiResponse.success("Employer status updated successfully", response));
    }

    // ==================== UPDATE ROLE ====================

    @PatchMapping("/employers/{id}/role")
    @Operation(
            summary = "Update employer role",
            description = "Changes the role of an employer"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> updateRole(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id,

            @Valid @RequestBody UpdateRoleRequest request) {

        EmployerResponse response = employerService.updateRole(id, request.getRole());

        return ResponseEntity
                .ok(ApiResponse.success("Employer role updated successfully", response));
    }

    // ==================== SET PRIMARY CONTACT ====================

    @PatchMapping("/employers/{id}/primary")
    @Operation(
            summary = "Set as primary contact",
            description = "Marks an employer as the primary contact for the organization"
    )
    public ResponseEntity<ApiResponse<EmployerResponse>> setPrimaryContact(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id) {

        EmployerResponse response = employerService.setPrimaryContact(id);

        return ResponseEntity
                .ok(ApiResponse.success("Employer set as primary contact", response));
    }

    // ==================== CHECK EMAIL ====================

    @GetMapping("/employers/check")
    @Operation(
            summary = "Check if email exists",
            description = "Validates if an email is already registered"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @Parameter(description = "Email to check", required = true)
            @RequestParam String email) {

        boolean exists = employerService.existsByEmail(email);

        return ResponseEntity
                .ok(ApiResponse.success(
                        exists ? "Email already exists" : "Email is available",
                        exists
                ));
    }

    // ==================== UPDATE LAST LOGIN ====================

    @PatchMapping("/employers/{id}/last-login")
    @Operation(
            summary = "Update last login",
            description = "Updates the last login timestamp for an employer"
    )
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(
            @Parameter(description = "Employer UUID")
            @PathVariable UUID id) {

        employerService.updateLastLogin(id);

        return ResponseEntity
                .ok(ApiResponse.success("Last login updated successfully"));
    }
}

