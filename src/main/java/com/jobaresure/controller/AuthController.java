package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.auth.AuthResponse;
import com.jobaresure.dto.auth.ForgotPasswordRequest;
import com.jobaresure.dto.auth.LoginRequest;
import com.jobaresure.dto.auth.RefreshTokenRequest;
import com.jobaresure.dto.auth.RegisterCompanyRequest;
import com.jobaresure.dto.auth.RegisterJobSeekerRequest;
import com.jobaresure.dto.auth.ResetPasswordRequest;
import com.jobaresure.dto.auth.SendEmailVerificationRequest;
import com.jobaresure.dto.auth.UserSummary;
import com.jobaresure.dto.auth.VerifyEmailRequest;
import com.jobaresure.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token refresh & current user")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/job-seeker")
    @Operation(summary = "Register a new job seeker account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerJobSeeker(
            @Valid @RequestBody RegisterJobSeekerRequest request) {
        AuthResponse data = authService.registerJobSeeker(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", data));
    }

    @PostMapping("/register/company")
    @Operation(summary = "Register a new company account (creates organization + primary contact)")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCompany(
            @Valid @RequestBody RegisterCompanyRequest request) {
        AuthResponse data = authService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", data));
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with email & password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", data));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access + refresh token pair")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", data));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the currently authenticated user")
    public ResponseEntity<ApiResponse<UserSummary>> me() {
        return ResponseEntity.ok(ApiResponse.success("Current user", authService.getCurrentUser()));
    }

    @PostMapping("/email/send-verification")
    @Operation(summary = "Send an email-verification code to an account's email")
    public ResponseEntity<ApiResponse<Void>> sendEmailVerification(
            @Valid @RequestBody SendEmailVerificationRequest request) {
        authService.sendEmailVerification(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists for that email, a verification code has been sent"));
    }

    @PostMapping("/email/verify")
    @Operation(summary = "Verify an email address with the emailed code")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    @PostMapping("/password/forgot")
    @Operation(summary = "Request a password-reset code by email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists for that email, a reset code has been sent"));
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset the password using the emailed code")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please log in."));
    }
}
