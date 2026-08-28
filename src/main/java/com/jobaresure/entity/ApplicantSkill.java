package com.jobaresure.entity;

import com.jobaresure.enums.ProficiencyLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** Join between an Applicant and a catalog Skill, with proficiency metadata. */
@Entity
@Table(name = "applicant_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"applicant_id", "skill_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSkill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProficiencyLevel proficiency;

    private Integer yearsOfExperience;
}
