package com.jobaresure.entity;

import com.jobaresure.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A one-time password issued to an identifier (phone number for now).
 * The code itself is stored hashed; we never persist the plaintext OTP.
 */
@Entity
@Table(name = "otp_tokens",
        indexes = {
                @Index(name = "idx_otp_identifier_purpose", columnList = "identifier,purpose")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    /** Phone number (E.164) or email the OTP was sent to. */
    @Column(nullable = false, length = 255)
    private String identifier;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean consumed = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
