package com.jobaresure.service;

import com.jobaresure.dto.auth.AuthResponse;
import com.jobaresure.dto.auth.LoginRequest;
import com.jobaresure.dto.auth.RegisterCompanyRequest;
import com.jobaresure.dto.auth.RegisterJobSeekerRequest;
import com.jobaresure.dto.auth.UserSummary;

public interface AuthService {

    AuthResponse registerJobSeeker(RegisterJobSeekerRequest request);

    AuthResponse registerCompany(RegisterCompanyRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    UserSummary getCurrentUser();

    /** Send an email-verification code to the address (no-op if no such account). */
    void sendEmailVerification(String email);

    /** Verify an email-verification code and flip the account's emailVerified flag. */
    void verifyEmail(String email, String code);

    /** Send a password-reset code to the address (no-op if no such account). */
    void forgotPassword(String email);

    /** Verify a password-reset code and set the account's new password. */
    void resetPassword(String email, String code, String newPassword);
}
