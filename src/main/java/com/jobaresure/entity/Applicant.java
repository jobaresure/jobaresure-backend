package com.jobaresure.entity;

import com.jobaresure.enums.NoticePeriod;
import com.jobaresure.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "applicants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "CHAR(36)", unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String profilePicture;
    private String headline;

    @Column(length = 500)
    private String bio;

    private String currentLocation;

    @Column(precision = 3, scale = 1)
    private BigDecimal totalExperienceYears;

    /*private Double currentSalary;
    private Double expectedSalary;*/

    @Column(precision = 10, scale = 2)
    private BigDecimal currentSalary;

    @Column(precision = 10, scale = 2)
    private BigDecimal expectedSalary;

    private String currency = "USD";

    private String resumeUrl;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    private NoticePeriod noticePeriod;

    private Boolean isActivelyLooking = true;

    private Integer profileCompleted = 0;

    @Builder.Default
    private Boolean identityVerified = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicantSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<Application> applications;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences;

    @OneToMany(
            mappedBy = "applicant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Education> educations = new ArrayList<>();
}
