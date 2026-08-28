package com.jobaresure.service.email;

import com.jobaresure.enums.OtpPurpose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Default provider for local development & tests: logs the code instead of
 * sending it. Active unless {@code app.email.provider} is set to a real gateway.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "mock", matchIfMissing = true)
public class MockEmailProvider implements EmailProvider {

    @Override
    public void sendCode(String email, String code, OtpPurpose purpose) {
        log.info("[MOCK EMAIL] {} code for {} is {} (configure app.email.provider to send real email)",
                purpose, email, code);
    }
}
