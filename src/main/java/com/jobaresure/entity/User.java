package com.jobaresure.entity;

import com.jobaresure.enums.Status;
import com.jobaresure.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Authentication identity for the platform. Profile data lives in the
 * role-specific entities (Applicant / Employer), which reference this row
 * via their {@code userId} column. This keeps auth concerns isolated from
 * the existing profile model.
 */
@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_phone", columnList = "phone")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.active;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    @Builder.Default
    private Boolean phoneVerified = false;

    /** Convenience pointer to the Applicant.id or Employer.id for this user. */
    @Column(name = "profile_id", columnDefinition = "CHAR(36)")
    private UUID profileId;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}
