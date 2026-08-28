package com.jobaresure.service.sms;

/**
 * Abstraction over the SMS gateway so OTP delivery is provider-agnostic.
 * Swap implementations via the {@code app.sms.provider} property.
 */
public interface SmsProvider {

    /**
     * Deliver a one-time password to a phone number.
     *
     * @param phone E.164 formatted number (e.g. +919876543210)
     * @param code  the plaintext OTP to deliver
     */
    void sendOtp(String phone, String code);
}
