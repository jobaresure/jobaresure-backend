package com.jobaresure.security;

import com.jobaresure.entity.User;
import com.jobaresure.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/** Convenience accessors for the currently authenticated user. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<CustomUserDetails> getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            return Optional.empty();
        }
        return Optional.of(details);
    }

    public static User getCurrentUser() {
        return getCurrentPrincipal()
                .map(CustomUserDetails::getUser)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user in context"));
    }
}
