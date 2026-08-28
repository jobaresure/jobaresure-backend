package com.jobaresure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {

    /** mock | smtp (real SMTP delivery to be wired in a later phase) */
    private String provider = "mock";

    /** From address used on outgoing verification / reset emails. */
    private String fromAddress = "no-reply@jobaresure.com";

    private String fromName = "Jobaresure";

    private Smtp smtp = new Smtp();

    @Data
    public static class Smtp {
        private String host;
        private Integer port = 587;
        private String username;
        private String password;
    }
}
