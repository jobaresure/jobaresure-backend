package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.profile.CompanyProfileResponse;
import com.jobaresure.dto.profile.EmployerProfileDto;
import com.jobaresure.dto.profile.UpdateCompanyProfileRequest;
import com.jobaresure.dto.profile.UpdateEmployerProfileRequest;
import com.jobaresure.service.CompanyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Company Profile", description = "Self-service company & employer profile management")
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @GetMapping("/me")
    @Operation(summary = "Get my organization profile")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> getMyCompany() {
        return ResponseEntity.ok(ApiResponse.success("Company retrieved", companyProfileService.getMyCompany()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my organization profile (company admin only)")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> updateMyCompany(
            @Valid @RequestBody UpdateCompanyProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Company updated",
                companyProfileService.updateMyCompany(request)));
    }

    @GetMapping("/me/employer")
    @Operation(summary = "Get my employer (recruiter) profile")
    public ResponseEntity<ApiResponse<EmployerProfileDto>> getMyEmployer() {
        return ResponseEntity.ok(ApiResponse.success("Employer retrieved", companyProfileService.getMyEmployer()));
    }

    @PutMapping("/me/employer")
    @Operation(summary = "Update my employer (recruiter) profile")
    public ResponseEntity<ApiResponse<EmployerProfileDto>> updateMyEmployer(
            @Valid @RequestBody UpdateEmployerProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employer updated",
                companyProfileService.updateMyEmployer(request)));
    }
}
