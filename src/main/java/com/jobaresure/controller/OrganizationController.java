package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.organization.CreateOrganizationRequest;
import com.jobaresure.dto.organization.OrganizationListResponse;
import com.jobaresure.dto.organization.OrganizationResponse;
import com.jobaresure.dto.organization.UpdateOrganizationRequest;
import com.jobaresure.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization Management", description = "APIs for managing organizations/companies")
public class OrganizationController {

    private final OrganizationService organizationService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(
            summary = "Create a new organization",
            description = "Creates a new company/organization in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Organization created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Organization with this name already exists"
            )
    })
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {

        OrganizationResponse response = organizationService.createOrganization(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Organization created successfully", response));
    }

    // ==================== GET SINGLE ====================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organization by ID",
            description = "Retrieves detailed information of a specific organization"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Organization found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Organization not found"
            )
    })
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationById(
            @Parameter(description = "Organization UUID", example = "a2f6c9e3-91c7-4d5b-9d5b-4c3b8a9d2a1f")
            @PathVariable UUID id) {

        OrganizationResponse response = organizationService.getOrganizationById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Organization retrieved successfully", response));
    }

    // ==================== GET ALL (PAGINATED) ====================

    @GetMapping
    @Operation(
            summary = "Get all organizations",
            description = "Retrieves paginated list of all organizations"
    )
    public ResponseEntity<ApiResponse<Page<OrganizationListResponse>>> getAllOrganizations(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        Page<OrganizationListResponse> organizations =
                organizationService.getAllOrganizations(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Organizations retrieved successfully", organizations));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @Operation(
            summary = "Update organization",
            description = "Updates an existing organization's information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Organization updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Organization not found"
            )
    })
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @Parameter(description = "Organization UUID")
            @PathVariable UUID id,

            @Valid @RequestBody UpdateOrganizationRequest request) {

        OrganizationResponse response = organizationService.updateOrganization(id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Organization updated successfully", response));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete organization",
            description = "Permanently deletes an organization from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Organization deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Organization not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @Parameter(description = "Organization UUID")
            @PathVariable UUID id) {

        organizationService.deleteOrganization(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Organization deleted successfully"));
    }

    // ==================== VERIFY ====================

    @PatchMapping("/{id}/verify")
    @Operation(
            summary = "Verify organization",
            description = "Marks an organization as verified by admin"
    )
    public ResponseEntity<ApiResponse<OrganizationResponse>> verifyOrganization(
            @Parameter(description = "Organization UUID")
            @PathVariable UUID id) {

        OrganizationResponse response = organizationService.verifyOrganization(id);

        return ResponseEntity
                .ok(ApiResponse.success("Organization verified successfully", response));
    }

    // ==================== CHECK EXISTS ====================

    @GetMapping("/check")
    @Operation(
            summary = "Check if organization exists",
            description = "Checks if an organization with the given name exists"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkOrganizationExists(
            @Parameter(description = "Company name to check", required = true)
            @RequestParam String companyName) {

        boolean exists = organizationService.existsByCompanyName(companyName);

        return ResponseEntity
                .ok(ApiResponse.success(
                        exists ? "Organization exists" : "Organization not found",
                        exists
                ));
    }
}
