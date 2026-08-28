package com.jobaresure.entity;

import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "CHAR(36)", unique = true)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String profilePicture;

    @Column(length = 150)
    private String jobTitle;

    @Column(length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    private EmployerRole role = EmployerRole.recruiter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String permissions;

    @Enumerated(EnumType.STRING)
    private EmployerStatus status = EmployerStatus.pending;

    private Boolean isPrimaryContact = false;

    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "postedBy")
    private List<Job> jobs;
}
