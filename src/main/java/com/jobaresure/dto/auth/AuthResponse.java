package com.jobaresure.dto.auth;

import lombok.Builder;
import lombok.Data;

/** Returned on successful registration / login / token refresh. */
@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    /** Access-token lifetime in seconds (for the frontend to schedule refresh). */
    private long expiresIn;

    private UserSummary user;
}
