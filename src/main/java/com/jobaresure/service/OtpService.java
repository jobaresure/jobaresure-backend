package com.jobaresure.service;

import com.jobaresure.config.properties.OtpProperties;
import com.jobaresure.entity.OtpToken;
import com.jobaresure.enums.OtpPurpose;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.repository.OtpTokenRepository;
import com.jobaresure.repository.UserRepository;
import com.jobaresure.service.email.EmailProvider;
import com.jobaresure.service.sms.SmsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Issues and verifies mobile OTPs. Codes are stored hashed (BCrypt) and scoped
 * by {@link OtpPurpose}; verifying a PHONE_VERIFICATION code also flips the
 * matching user's {@code phoneVerified} flag.
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final SmsProvider smsProvider;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;
    private final OtpProperties otpProperties;

    /**
     * Issue a code to a phone number and deliver it over SMS. Runs in its own
     * transaction so a delivery failure can't poison a caller's transaction
     * (e.g. registration issuing a verification code).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOtp(String phone, OtpPurpose purpose) {
        smsProvider.sendOtp(phone, issueCode(phone, purpose));
    }

    /** Issue a code to an email address and deliver it over email (own transaction). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendEmailCode(String email, OtpPurpose purpose) {
        emailProvider.sendCode(email, issueCode(email, purpose), purpose);
    }

    /**
     * Generates, persists (hashed), and returns a fresh code for the given
     * identifier/purpose — enforcing the resend cooldown and superseding any
     * previously active codes. Delivery is the caller's responsibility.
     */
    private String issueCode(String identifier, OtpPurpose purpose) {
        // Enforce resend cooldown using the most recent active token.
        otpTokenRepository
                .findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(identifier, purpose)
                .ifPresent(existing -> {
                    LocalDateTime nextAllowed = existing.getCreatedAt()
                            .plusSeconds(otpProperties.getResendCooldownSeconds());
                    if (existing.getCreatedAt() != null && LocalDateTime.now().isBefore(nextAllowed)) {
                        throw new BadRequestException("Please wait before requesting another code.");
                    }
                });

        // Supersede any active tokens so only the newest code is valid.
        otpTokenRepository.invalidateActiveTokens(identifier, purpose);

        String code = generateCode();
        OtpToken token = OtpToken.builder()
                .identifier(identifier)
                .codeHash(passwordEncoder.encode(code))
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes()))
                .build();
        otpTokenRepository.save(token);
        return code;
    }

    @Transactional
    public void verifyOtp(String identifier, String code, OtpPurpose purpose) {
        OtpToken token = otpTokenRepository
                .findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(identifier, purpose)
                .orElseThrow(() -> new BadRequestException("No active code found. Please request a new one."));

        if (token.isExpired()) {
            throw new BadRequestException("Code has expired. Please request a new one.");
        }

        if (token.getAttempts() >= otpProperties.getMaxAttempts()) {
            token.setConsumed(true);
            otpTokenRepository.save(token);
            throw new BadRequestException("Too many incorrect attempts. Please request a new code.");
        }

        if (!passwordEncoder.matches(code, token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            otpTokenRepository.save(token);
            throw new BadRequestException("Invalid code.");
        }

        token.setConsumed(true);
        otpTokenRepository.save(token);

        if (purpose == OtpPurpose.PHONE_VERIFICATION) {
            userRepository.findByPhone(identifier).ifPresent(user -> {
                user.setPhoneVerified(true);
                userRepository.save(user);
            });
        } else if (purpose == OtpPurpose.EMAIL_VERIFICATION) {
            userRepository.findByEmail(identifier).ifPresent(user -> {
                user.setEmailVerified(true);
                userRepository.save(user);
            });
        }
    }

    private String generateCode() {
        int length = otpProperties.getLength();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
