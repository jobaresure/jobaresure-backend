package com.jobaresure.service.impl;

import com.jobaresure.common.Constants;
import com.jobaresure.dto.organization.CreateOrganizationRequest;
import com.jobaresure.dto.organization.OrganizationListResponse;
import com.jobaresure.dto.organization.OrganizationResponse;
import com.jobaresure.dto.organization.UpdateOrganizationRequest;
import com.jobaresure.entity.Organization;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.JobRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.service.OrganizationService;
import com.jobaresure.util.PublicIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Override
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        log.info("Creating new organization: {}", request.getCompanyName());

        // Check for duplicate company name
        if (organizationRepository.existsByCompanyName(request.getCompanyName())) {
            throw new DuplicateResourceException("Organization", "companyName", request.getCompanyName());
        }

        // Build organization entity
        Organization organization = Organization.builder()
                .publicId(PublicIdGenerator.generate(Constants.ORGANIZATION_PREFIX))
                .companyName(request.getCompanyName())
                .companyEmail(request.getCompanyEmail())
                .emailDomain(request.getEmailDomain())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .headquartersLocation(request.getHeadquartersLocation())
                .foundedYear(request.getFoundedYear())
                .description(request.getDescription())
                .linkedinUrl(request.getLinkedinUrl())
                .twitterUrl(request.getTwitterUrl())
                .build();

        // Generate slug
        organization.setCompanySlug(generateSlug(request.getCompanyName()));

        Organization saved = organizationRepository.save(organization);
        log.info("Organization created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /*@Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(UUID id) {
        log.info("Fetching organization with ID: {}", id);

        Organization organization = organizationRepository.findByIdWithStats(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

        return mapToResponse(organization);
    }*/

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(UUID id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

        long employersCount = employerRepository.countByOrganizationId(id);

        long activeJobsCount = jobRepository
                .countByOrganizationIdAndStatus(id, JobStatus.active);

        OrganizationResponse response = mapToResponse(organization);
        response.setEmployersCount(employersCount);
        response.setActiveJobsCount(activeJobsCount);

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationListResponse> getAllOrganizations(Pageable pageable) {

        Page<Organization> page = organizationRepository.findAll(pageable);

        return page.map(org -> {

            long employersCount = employerRepository.countByOrganizationId(org.getId());

            long activeJobsCount = jobRepository
                    .countByOrganizationIdAndStatus(org.getId(), JobStatus.active);

            OrganizationListResponse dto = mapToListResponse(org);
            dto.setEmployersCount(employersCount);
            dto.setActiveJobsCount(activeJobsCount);

            return dto;
        });
    }

    @Override
    public OrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {
        log.info("Updating organization with ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

        // Update fields if provided
        if (request.getCompanyName() != null) {
            // Check for duplicate if name changed
            if (!organization.getCompanyName().equals(request.getCompanyName()) &&
                    organizationRepository.existsByCompanyName(request.getCompanyName())) {
                throw new DuplicateResourceException("Organization", "companyName", request.getCompanyName());
            }
            organization.setCompanyName(request.getCompanyName());
            organization.setCompanySlug(generateSlug(request.getCompanyName()));
        }

        if (request.getCompanyEmail() != null) {
            organization.setCompanyEmail(request.getCompanyEmail());
        }

        if (request.getEmailDomain() != null) {
            organization.setEmailDomain(request.getEmailDomain());
        }

        if (request.getWebsite() != null) {
            organization.setWebsite(request.getWebsite());
        }

        if (request.getIndustry() != null) {
            organization.setIndustry(request.getIndustry());
        }

        if (request.getHeadquartersLocation() != null) {
            organization.setHeadquartersLocation(request.getHeadquartersLocation());
        }

        if (request.getFoundedYear() != null) {
            organization.setFoundedYear(request.getFoundedYear());
        }

        if (request.getDescription() != null) {
            organization.setDescription(request.getDescription());
        }

        if (request.getAbout() != null) {
            organization.setAbout(request.getAbout());
        }

        if (request.getLinkedinUrl() != null) {
            organization.setLinkedinUrl(request.getLinkedinUrl());
        }

        if (request.getTwitterUrl() != null) {
            organization.setTwitterUrl(request.getTwitterUrl());
        }

        Organization updated = organizationRepository.save(organization);
        log.info("Organization updated successfully: {}", id);

        return mapToResponse(updated);
    }

    @Override
    public void deleteOrganization(UUID id) {
        log.info("Deleting organization with ID: {}", id);

        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization", "id", id);
        }

        organizationRepository.deleteById(id);
        log.info("Organization deleted successfully: {}", id);
    }

    @Override
    public OrganizationResponse verifyOrganization(UUID id) {
        log.info("Verifying organization with ID: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

        organization.setIsVerified(true);
        Organization updated = organizationRepository.save(organization);

        log.info("Organization verified successfully: {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompanyName(String companyName) {
        return organizationRepository.existsByCompanyName(companyName);
    }

    // ==================== Mappers ====================

    private OrganizationResponse mapToResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .companyName(organization.getCompanyName())
                .companySlug(organization.getCompanySlug())
                .emailDomain(organization.getEmailDomain())
                .companyEmail(organization.getCompanyEmail())
                .companyPhone(organization.getCompanyPhone())
                .logoUrl(organization.getLogoUrl())
                .website(organization.getWebsite())
                .industry(organization.getIndustry())
                .companySize(organization.getCompanySize() != null ?
                        organization.getCompanySize().toString() : null)
                .description(organization.getDescription())
                .about(organization.getAbout())
                .headquartersLocation(organization.getHeadquartersLocation())
                .foundedYear(organization.getFoundedYear())
                .linkedinUrl(organization.getLinkedinUrl())
                .twitterUrl(organization.getTwitterUrl())
                .isVerified(organization.getIsVerified())
                .status(organization.getStatus() != null ?
                        organization.getStatus().toString() : null)
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }

    private OrganizationListResponse mapToListResponse(Organization organization) {
        return OrganizationListResponse.builder()
                .id(organization.getId())
                .companyName(organization.getCompanyName())
                .logoUrl(organization.getLogoUrl())
                .industry(organization.getIndustry())
                .headquartersLocation(organization.getHeadquartersLocation())
                .isVerified(organization.getIsVerified())
                .status(organization.getStatus() != null ?
                        organization.getStatus().toString() : null)
                .createdAt(organization.getCreatedAt())
                .build();
    }

    private String generateSlug(String companyName) {
        return companyName
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}

