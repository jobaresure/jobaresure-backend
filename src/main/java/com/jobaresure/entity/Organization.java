package com.jobaresure.entity;


import com.jobaresure.enums.CompanySize;
import com.jobaresure.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "public_id", nullable = false, unique = true, length = 50)
    private String publicId;

    @Column(nullable = false, unique = true)
    private String companyName;

    @Column(unique = true)
    private String companySlug;

    private String emailDomain;

    @Column(nullable = false)
    private String companyEmail;

    private String companyPhone;
    private String logoUrl;
    private String website;
    private String industry;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String about;

    private String headquartersLocation;
    private Integer foundedYear;

    private String linkedinUrl;
    private String twitterUrl;

    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Employer> employers = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Job> jobs = new ArrayList<>();
}
