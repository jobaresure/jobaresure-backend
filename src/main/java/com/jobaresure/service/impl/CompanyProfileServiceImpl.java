package com.jobaresure.service.impl;

import com.jobaresure.dto.profile.CompanyProfileResponse;
import com.jobaresure.dto.profile.EmployerProfileDto;
import com.jobaresure.dto.profile.UpdateCompanyProfileRequest;
import com.jobaresure.dto.profile.UpdateEmployerProfileRequest;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Organization;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.security.SecurityUtils;
import com.jobaresure.service.CompanyProfileService;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private final EmployerRepository employerRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getMyCompany() {
        Employer employer = currentEmployer();
        return toResponse(employer.getOrganization(), employer);
    }

    @Override
    @Transactional
    public CompanyProfileResponse updateMyCompany(UpdateCompanyProfileRequest req) {
        Employer employer = currentEmployer();
        if (employer.getRole() != EmployerRole.admin) {
            throw new AccessDeniedException("Only a company admin can edit the organization profile");
        }
        Organization org = employer.getOrganization();

        if (req.getCompanyName() != null && !req.getCompanyName().equals(org.getCompanyName())) {
            if (organizationRepository.existsByCompanyName(req.getCompanyName())) {
                throw new DuplicateResourceException("A company with this name already exists");
            }
            org.setCompanyName(req.getCompanyName());
        }
        if (req.getCompanyEmail() != null) org.setCompanyEmail(req.getCompanyEmail());
        if (req.getCompanyPhone() != null) org.setCompanyPhone(req.getCompanyPhone());
        if (req.getLogoUrl() != null) org.setLogoUrl(req.getLogoUrl());
        if (req.getWebsite() != null) org.setWebsite(req.getWebsite());
        if (req.getIndustry() != null) org.setIndustry(req.getIndustry());
        if (req.getCompanySize() != null) org.setCompanySize(req.getCompanySize());
        if (req.getDescription() != null) org.setDescription(req.getDescription());
        if (req.getAbout() != null) org.setAbout(req.getAbout());
        if (req.getHeadquartersLocation() != null) org.setHeadquartersLocation(req.getHeadquartersLocation());
        if (req.getFoundedYear() != null) org.setFoundedYear(req.getFoundedYear());
        if (req.getLinkedinUrl() != null) org.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getTwitterUrl() != null) org.setTwitterUrl(req.getTwitterUrl());

        organizationRepository.save(org);
        return toResponse(org, employer);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployerProfileDto getMyEmployer() {
        return EmployerProfileDto.from(currentEmployer());
    }

    @Override
    @Transactional
    public EmployerProfileDto updateMyEmployer(UpdateEmployerProfileRequest req) {
        Employer e = currentEmployer();
        if (req.getFirstName() != null) e.setFirstName(req.getFirstName());
        if (req.getLastName() != null) e.setLastName(req.getLastName());
        if (req.getPhone() != null) e.setPhone(req.getPhone());
        if (req.getProfilePicture() != null) e.setProfilePicture(req.getProfilePicture());
        if (req.getJobTitle() != null) e.setJobTitle(req.getJobTitle());
        if (req.getDepartment() != null) e.setDepartment(req.getDepartment());
        return EmployerProfileDto.from(employerRepository.save(e));
    }

    // ---- Helpers ----

    private Employer currentEmployer() {
        UUID userId = SecurityUtils.getCurrentUser().getId();
        return employerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));
    }

    private CompanyProfileResponse toResponse(Organization org, Employer currentEmployer) {
        return CompanyProfileResponse.builder()
                .id(org.getId())
                .publicId(org.getPublicId())
                .companyName(org.getCompanyName())
                .companySlug(org.getCompanySlug())
                .companyEmail(org.getCompanyEmail())
                .companyPhone(org.getCompanyPhone())
                .logoUrl(org.getLogoUrl())
                .website(org.getWebsite())
                .industry(org.getIndustry())
                .companySize(org.getCompanySize())
                .description(org.getDescription())
                .about(org.getAbout())
                .headquartersLocation(org.getHeadquartersLocation())
                .foundedYear(org.getFoundedYear())
                .linkedinUrl(org.getLinkedinUrl())
                .twitterUrl(org.getTwitterUrl())
                .isVerified(org.getIsVerified())
                .status(org.getStatus())
                .currentEmployer(EmployerProfileDto.from(currentEmployer))
                .build();
    }
}
