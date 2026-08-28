package com.jobaresure.entity;

import com.jobaresure.enums.job.EducationLevel;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.enums.job.JobType;
import com.jobaresure.enums.WorkMode;
import com.jobaresure.enums.job.SeniorityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "public_id", nullable = false, unique = true, length = 50)
    private String publicId;

    @Column(nullable = false)
    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private Employer postedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkMode workMode;

    private String location;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryMax;

    @Builder.Default
    private String salaryCurrency = "USD";

    @Builder.Default
    private Boolean isSalaryVisible = true;

    private Integer experienceMin;
    private Integer experienceMax;

    private LocalDate applicationDeadline;

    @Builder.Default
    private Integer totalApplications = 0;

    @Builder.Default
    private Integer viewsCount = 0;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String preferredQualifications;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(columnDefinition = "TEXT")
    private String applicationInstructions;

    @Builder.Default
    private Integer numberOfOpenings = 1;

    private String department;

    @Enumerated(EnumType.STRING)
    private SeniorityLevel seniorityLevel;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.draft;

    @Builder.Default
    private Boolean isFeatured = false;

    @Builder.Default
    private Boolean isUrgent = false;

    private String externalJobUrl;

    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;
}