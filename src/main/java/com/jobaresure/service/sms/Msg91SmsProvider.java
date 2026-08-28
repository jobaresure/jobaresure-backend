package com.jobaresure.service.sms;

import com.jobaresure.config.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * MSG91 (https://msg91.com) SMS gateway implementation, suited to the Indian
 * market. Uses the v5 "flow" API to deliver a templated OTP message. The
 * template (with a single {@code otp} variable) and sender id are created in
 * the MSG91 dashboard and configured via environment variables.
 *
 * Active when {@code app.sms.provider=msg91}.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "msg91")
public class Msg91SmsProvider implements SmsProvider {

    private final SmsProperties.Msg91 config;
    private final RestClient restClient;

    public Msg91SmsProvider(SmsProperties smsProperties) {
        this.config = smsProperties.getMsg91();
        this.restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }

    @Override
    public void sendOtp(String phone, String code) {
        String mobile = phone.startsWith("+") ? phone.substring(1) : phone;

        Map<String, Object> body = Map.of(
                "template_id", config.getOtpTemplateId(),
                "sender", config.getSenderId(),
                "short_url", "0",
                "recipients", new Object[]{
                        Map.of("mobiles", mobile, "otp", code)
                });

        try {
            restClient.post()
                    .uri("/flow")
                    .header("authkey", config.getAuthKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("MSG91 OTP dispatched to {}", maskPhone(phone));
        } catch (Exception ex) {
            log.error("MSG91 OTP dispatch failed for {}: {}", maskPhone(phone), ex.getMessage());
            throw new IllegalStateException("Failed to send OTP. Please try again.", ex);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
