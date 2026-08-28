package com.jobaresure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Master catalog of skills (e.g. "Plumbing", "Welding", "Spoken English").
 * Shared across applicants and jobs so matching can compare normalized values.
 */
@Entity
@Table(name = "skills",
        indexes = @Index(name = "idx_skills_slug", columnList = "slug"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    /** Normalized lowercase key used for de-duplication & matching. */
    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(length = 80)
    private String category;
}
