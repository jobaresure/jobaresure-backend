package com.jobaresure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.sms")
public class SmsProperties {

    /** mock | msg91 */
    private String provider = "mock";

    private Msg91 msg91 = new Msg91();

    @Data
    public static class Msg91 {
        private String baseUrl = "https://control.msg91.com/api/v5";
        private String authKey;
        private String otpTemplateId;
        private String senderId;
    }
}
