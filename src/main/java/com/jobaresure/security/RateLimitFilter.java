package com.jobaresure.security;

import tools.jackson.databind.ObjectMapper;
import com.jobaresure.config.properties.RateLimitProperties;
import com.jobaresure.dto.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight per-IP token-bucket rate limiter applied to the sensitive
 * auth/OTP endpoints. In-memory by design for a single node; swap the
 * {@link #buckets} map for a Redis-backed store when scaling horizontally.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties props;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.isEnabled()) {
            return true;
        }
        String path = request.getRequestURI();
        return !(path.startsWith("/api/v1/auth") || path.startsWith("/api/v1/otp"));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        Bucket bucket = buckets.computeIfAbsent(clientKey(request), k -> new Bucket(props));
        if (bucket.tryConsume()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // Too Many Requests
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    ApiResponse.error("Too many requests. Please slow down and try again shortly."));
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /** Simple thread-safe token bucket with time-based refill. */
    private static final class Bucket {
        private final long capacity;
        private final long refillTokens;
        private final long refillPeriodNanos;
        private double tokens;
        private long lastRefillNanos;

        Bucket(RateLimitProperties props) {
            this.capacity = props.getCapacity();
            this.refillTokens = props.getRefillTokens();
            this.refillPeriodNanos = props.getRefillPeriodSeconds() * 1_000_000_000L;
            this.tokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsed = now - lastRefillNanos;
            if (elapsed <= 0) {
                return;
            }
            double added = ((double) elapsed / refillPeriodNanos) * refillTokens;
            if (added > 0) {
                tokens = Math.min(capacity, tokens + added);
                lastRefillNanos = now;
            }
        }
    }
}
