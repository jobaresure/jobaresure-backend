package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.auth.SendOtpRequest;
import com.jobaresure.dto.auth.VerifyOtpRequest;
import com.jobaresure.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Tag(name = "OTP", description = "Mobile OTP send & verify")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    @Operation(summary = "Send an OTP to a phone number for the given purpose")
    public ResponseEntity<ApiResponse<Void>> send(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtp(request.getPhone(), request.getPurpose());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify an OTP code for a phone number")
    public ResponseEntity<ApiResponse<Void>> verify(@Valid @RequestBody VerifyOtpRequest request) {
        otpService.verifyOtp(request.getPhone(), request.getCode(), request.getPurpose());
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully"));
    }
}
