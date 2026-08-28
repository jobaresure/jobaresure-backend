package com.jobaresure.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Default provider for local development & tests: logs the OTP instead of
 * sending it. Active unless {@code app.sms.provider=msg91}.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsProvider implements SmsProvider {

    @Override
    public void sendOtp(String phone, String code) {
        log.info("[MOCK SMS] OTP for {} is {} (configure app.sms.provider=msg91 to send real SMS)", phone, code);
    }
}
