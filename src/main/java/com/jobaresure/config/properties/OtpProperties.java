package com.jobaresure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.otp")
public class OtpProperties {
    private int length = 6;
    private int expiryMinutes = 5;
    private int maxAttempts = 5;
    private int resendCooldownSeconds = 30;
}
