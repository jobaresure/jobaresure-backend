package com.jobaresure.service.email;

import com.jobaresure.enums.OtpPurpose;

/**
 * Abstraction over the email gateway so verification / password-reset codes are
 * provider-agnostic. Swap implementations via the {@code app.email.provider}
 * property (mirrors {@link com.jobaresure.service.sms.SmsProvider}).
 */
public interface EmailProvider {

    /**
     * Deliver a one-time code to an email address.
     *
     * @param email   recipient email address
     * @param code    the plaintext code to deliver
     * @param purpose why the code was issued (drives subject / copy)
     */
    void sendCode(String email, String code, OtpPurpose purpose);
}
