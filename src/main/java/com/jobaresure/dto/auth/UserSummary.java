package com.jobaresure.dto.auth;

import com.jobaresure.entity.User;
import com.jobaresure.enums.Status;
import com.jobaresure.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/** Public-safe view of a User (never exposes the password hash). */
@Data
@Builder
public class UserSummary {

    private UUID id;
    private String email;
    private String phone;
    private UserRole role;
    private Status status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private UUID profileId;

    public static UserSummary from(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .profileId(user.getProfileId())
                .build();
    }
}
