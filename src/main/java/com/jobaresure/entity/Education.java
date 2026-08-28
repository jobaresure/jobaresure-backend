package com.jobaresure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String fieldOfStudy;

    @Column(nullable = false)
    private String institutionName;

    private String location;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isCurrent = false;

    private String grade;

    @Column(columnDefinition = "TEXT")
    private String description;
}