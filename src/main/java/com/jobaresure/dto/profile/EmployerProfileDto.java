package com.jobaresure.dto.profile;

import com.jobaresure.entity.Employer;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployerProfileDto {

    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePicture;
    private String jobTitle;
    private String department;
    private EmployerRole role;
    private EmployerStatus status;
    private Boolean isPrimaryContact;

    public static EmployerProfileDto from(Employer e) {
        return EmployerProfileDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .profilePicture(e.getProfilePicture())
                .jobTitle(e.getJobTitle())
                .department(e.getDepartment())
                .role(e.getRole())
                .status(e.getStatus())
                .isPrimaryContact(e.getIsPrimaryContact())
                .build();
    }
}
