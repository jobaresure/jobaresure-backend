package com.jobaresure.enums;

/**
 * Top-level access roles for the platform.
 * Mapped to Spring Security authorities as ROLE_JOB_SEEKER / ROLE_COMPANY / ROLE_ADMIN.
 */
public enum UserRole {
    JOB_SEEKER,
    COMPANY,
    ADMIN
}
