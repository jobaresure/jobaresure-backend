package com.jobaresure.service.impl;

import com.jobaresure.common.Constants;
import com.jobaresure.dto.auth.AuthResponse;
import com.jobaresure.dto.auth.LoginRequest;
import com.jobaresure.dto.auth.RegisterCompanyRequest;
import com.jobaresure.dto.auth.RegisterJobSeekerRequest;
import com.jobaresure.dto.auth.UserSummary;
import com.jobaresure.entity.Applicant;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Organization;
import com.jobaresure.entity.User;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import com.jobaresure.enums.OtpPurpose;
import com.jobaresure.enums.Status;
import com.jobaresure.enums.UserRole;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.UnauthorizedException;
import com.jobaresure.repository.ApplicantRepository;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.repository.UserRepository;
import com.jobaresure.security.JwtService;
import com.jobaresure.security.SecurityUtils;
import com.jobaresure.service.AuthService;
import com.jobaresure.service.OtpService;
import com.jobaresure.util.PublicIdGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Override
    @Transactional
    public AuthResponse registerJobSeeker(RegisterJobSeekerRequest request) {
        guardUniqueEmailAndPhone(request.getEmail(), request.getPhone());

        User user = userRepository.save(User.builder()
                .email(normalizeEmail(request.getEmail()))
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.JOB_SEEKER)
                .status(Status.active)
                .build());

        Applicant applicant = applicantRepository.save(Applicant.builder()
                .userId(user.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(Status.active)
                .build());

        user.setProfileId(applicant.getId());
        userRepository.save(user);

        sendPhoneVerificationSilently(user.getPhone());
        sendEmailVerificationSilently(user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        guardUniqueEmailAndPhone(request.getEmail(), request.getPhone());
        if (organizationRepository.existsByCompanyName(request.getCompanyName())) {
            throw new DuplicateResourceException("A company with this name already exists");
        }

        User user = userRepository.save(User.builder()
                .email(normalizeEmail(request.getEmail()))
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.COMPANY)
                .status(Status.active)
                .build());

        Organization organization = organizationRepository.save(Organization.builder()
                .publicId(PublicIdGenerator.generate(Constants.ORGANIZATION_PREFIX))
                .companyName(request.getCompanyName())
                .companySlug(uniqueSlug(request.getCompanyName()))
                .companyEmail(request.getCompanyEmail())
                .isVerified(false)
                .status(Status.active)
                .build());

        Employer employer = employerRepository.save(Employer.builder()
                .userId(user.getId())
                .organization(organization)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(EmployerRole.admin)
                .status(EmployerStatus.pending)
                .isPrimaryContact(true)
                .build());

        user.setProfileId(employer.getId());
        userRepository.save(user);

        sendPhoneVerificationSilently(user.getPhone());
        sendEmailVerificationSilently(user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (DisabledException ex) {
            throw new UnauthorizedException("Your account is inactive. Please contact support.");
        } catch (LockedException ex) {
            throw new UnauthorizedException("Your account has been suspended.");
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        final Claims claims;
        try {
            claims = jwtService.parse(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        if (!JwtService.TYPE_REFRESH.equals(jwtService.extractType(claims))) {
            throw new UnauthorizedException("Provided token is not a refresh token");
        }

        UUID userId = jwtService.extractUserId(claims);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Account no longer exists"));

        if (user.getStatus() != Status.active) {
            throw new UnauthorizedException("Account is not active");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummary getCurrentUser() {
        return UserSummary.from(SecurityUtils.getCurrentUser());
    }

    @Override
    @Transactional
    public void sendEmailVerification(String email) {
        String normalized = normalizeEmail(email);
        // Respond generically regardless; only issue a code for a real, unverified account.
        userRepository.findByEmail(normalized).ifPresent(user -> {
            if (!Boolean.TRUE.equals(user.getEmailVerified())) {
                otpService.sendEmailCode(normalized, OtpPurpose.EMAIL_VERIFICATION);
            }
        });
    }

    @Override
    @Transactional
    public void verifyEmail(String email, String code) {
        // verifyOtp flips emailVerified for the matching account on success.
        otpService.verifyOtp(normalizeEmail(email), code, OtpPurpose.EMAIL_VERIFICATION);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        String normalized = normalizeEmail(email);
        // Never reveal whether the email is registered; only send if it is.
        userRepository.findByEmail(normalized)
                .ifPresent(user -> otpService.sendEmailCode(normalized, OtpPurpose.PASSWORD_RESET));
    }

    @Override
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        String normalized = normalizeEmail(email);
        // Throws BadRequest if the code is missing/expired/incorrect.
        otpService.verifyOtp(normalized, code, OtpPurpose.PASSWORD_RESET);

        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset code"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ---------------------------------------------------------------------

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .user(UserSummary.from(user))
                .build();
    }

    private void guardUniqueEmailAndPhone(String email, String phone) {
        if (userRepository.existsByEmail(normalizeEmail(email))) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("An account with this phone number already exists");
        }
    }

    private void sendPhoneVerificationSilently(String phone) {
        if (phone == null) {
            return;
        }
        try {
            otpService.sendOtp(phone, OtpPurpose.PHONE_VERIFICATION);
        } catch (Exception ex) {
            // Registration should still succeed if SMS delivery hiccups; the user
            // can request a fresh OTP from the verification screen.
            log.warn("Could not send verification OTP during registration: {}", ex.getMessage());
        }
    }

    private void sendEmailVerificationSilently(String email) {
        if (email == null) {
            return;
        }
        try {
            otpService.sendEmailCode(email, OtpPurpose.EMAIL_VERIFICATION);
        } catch (Exception ex) {
            // Registration should still succeed if email delivery hiccups; the user
            // can request a fresh code from the verification screen.
            log.warn("Could not send verification email during registration: {}", ex.getMessage());
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String uniqueSlug(String companyName) {
        String base = companyName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "company";
        }
        String slug = base;
        while (organizationRepository.findByCompanySlug(slug).isPresent()) {
            slug = base + "-" + PublicIdGenerator.generate("").substring(0, 4).toLowerCase(Locale.ROOT);
        }
        return slug;
    }
}
