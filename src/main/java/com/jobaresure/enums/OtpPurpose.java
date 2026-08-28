package com.jobaresure.enums;

/**
 * Reason an OTP was issued. Keeps codes scoped so a login OTP cannot be
 * replayed to verify a phone number, etc.
 */
public enum OtpPurpose {
    PHONE_VERIFICATION,
    EMAIL_VERIFICATION,
    LOGIN,
    PASSWORD_RESET
}
