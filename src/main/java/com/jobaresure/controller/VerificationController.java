package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.verification.SubmitVerificationRequest;
import com.jobaresure.dto.verification.VerificationResponse;
import com.jobaresure.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Verification", description = "Identity (job seeker) & company verification submissions")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/identity")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(summary = "Submit an identity verification (job seeker)")
    public ResponseEntity<ApiResponse<VerificationResponse>> submitIdentity(
            @Valid @RequestBody SubmitVerificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Identity verification submitted",
                        verificationService.submitIdentity(request)));
    }

    @PostMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Submit a company verification (company admin)")
    public ResponseEntity<ApiResponse<VerificationResponse>> submitCompany(
            @Valid @RequestBody SubmitVerificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company verification submitted",
                        verificationService.submitCompany(request)));
    }

    @GetMapping("/me")
    @Operation(summary = "List my verification submissions")
    public ResponseEntity<ApiResponse<List<VerificationResponse>>> myVerifications() {
        return ResponseEntity.ok(ApiResponse.success("Verifications retrieved",
                verificationService.getMyVerifications()));
    }
}
