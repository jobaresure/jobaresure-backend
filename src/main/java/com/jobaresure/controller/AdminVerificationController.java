package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.verification.ReviewVerificationRequest;
import com.jobaresure.dto.verification.VerificationResponse;
import com.jobaresure.enums.VerificationStatus;
import com.jobaresure.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/verifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin · Verification", description = "Review identity & company verification requests")
public class AdminVerificationController {

    private final VerificationService verificationService;

    @GetMapping
    @Operation(summary = "List verification requests, optionally filtered by status")
    public ResponseEntity<ApiResponse<Page<VerificationResponse>>> list(
            @RequestParam(required = false) VerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success("Verifications retrieved",
                verificationService.adminList(status, pageable)));
    }

    @PatchMapping("/{id}/review")
    @Operation(summary = "Approve / reject / request resubmission for a verification request")
    public ResponseEntity<ApiResponse<VerificationResponse>> review(
            @PathVariable UUID id, @Valid @RequestBody ReviewVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Verification reviewed",
                verificationService.adminReview(id, request)));
    }
}
