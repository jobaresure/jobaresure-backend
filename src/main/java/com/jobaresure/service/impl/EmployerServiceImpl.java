package com.jobaresure.service.impl;

import com.jobaresure.dto.employer.CreateEmployerRequest;
import com.jobaresure.dto.employer.EmployerListResponse;
import com.jobaresure.dto.employer.EmployerResponse;
import com.jobaresure.dto.employer.UpdateEmployerRequest;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Organization;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.JobRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.service.EmployerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final OrganizationRepository organizationRepository;
    private final JobRepository jobRepository;

    @Override
    public EmployerResponse createEmployer(UUID organizationId, CreateEmployerRequest request) {
        log.info("Creating employer for organization: {}", organizationId);

        // Check if organization exists
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        // Check for duplicate email
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employer", "email", request.getEmail());
        }

        // Build employer
        Employer employer = Employer.builder()
                .organization(organization)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .jobTitle(request.getJobTitle())
                .department(request.getDepartment())
                .profilePicture(request.getProfilePicture())
                .role(request.getRole() != null ? request.getRole() : EmployerRole.recruiter)
                .status(EmployerStatus.pending)
                .isPrimaryContact(false)
                .build();

        Employer saved = employerRepository.save(employer);
        log.info("Employer created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
//    @Transactional(readOnly = true)
    public EmployerResponse getEmployerById(UUID id) {
        log.info("Fetching employer with ID: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        long jobsPosted = jobRepository.countByPostedById(id);

        EmployerResponse response = mapToResponse(employer);
        response.setTotalJobsPosted(jobsPosted);

        return response;
    }

//    @Transactional(readOnly = true)
    @Override
    public Page<EmployerListResponse> getAllEmployers(Pageable pageable) {
        log.info("Fetching all employers with pagination: {}", pageable);

        return employerRepository.findAll(pageable)
                .map(this::mapToListResponse);
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<EmployerListResponse> getEmployersByOrganization(UUID organizationId, Pageable pageable) {
        log.info("Fetching employers for organization: {}", organizationId);

        // Verify organization exists
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", "id", organizationId);
        }

        return employerRepository.findByOrganizationId(organizationId, pageable)
                .map(this::mapToListResponse);
    }

    @Override
    public EmployerResponse updateEmployer(UUID id, UpdateEmployerRequest request) {
        log.info("Updating employer with ID: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            employer.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            employer.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            employer.setPhone(request.getPhone());
        }

        if (request.getJobTitle() != null) {
            employer.setJobTitle(request.getJobTitle());
        }

        if (request.getDepartment() != null) {
            employer.setDepartment(request.getDepartment());
        }

        if (request.getRole() != null) {
            employer.setRole(request.getRole());
        }

        if (request.getStatus() != null) {
            employer.setStatus(request.getStatus());
        }

        if (request.getProfilePicture() != null) {
            employer.setProfilePicture(request.getProfilePicture());
        }

        Employer updated = employerRepository.save(employer);
        log.info("Employer updated successfully: {}", id);

        return mapToResponse(updated);
    }

    @Override
    public void deleteEmployer(UUID id) {
        log.info("Deleting employer with ID: {}", id);

        if (!employerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employer", "id", id);
        }

        employerRepository.deleteById(id);
        log.info("Employer deleted successfully: {}", id);
    }

    @Override
    public EmployerResponse updateStatus(UUID id, EmployerStatus status) {
        log.info("Updating status for employer: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        employer.setStatus(status);
        Employer updated = employerRepository.save(employer);

        log.info("Employer status updated to: {}", status);
        return mapToResponse(updated);
    }

    @Override
    public EmployerResponse updateRole(UUID id, EmployerRole role) {
        log.info("Updating role for employer: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        employer.setRole(role);
        Employer updated = employerRepository.save(employer);

        log.info("Employer role updated to: {}", role);
        return mapToResponse(updated);
    }

    @Override
    public EmployerResponse setPrimaryContact(UUID id) {
        log.info("Setting primary contact for employer: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        // Unset previous primary contact if any
        employer.setIsPrimaryContact(true);
        Employer updated = employerRepository.save(employer);

        log.info("Employer set as primary contact: {}", id);
        return mapToResponse(updated);
    }

    @Override
//    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return employerRepository.existsByEmail(email);
    }

    @Override
    public void updateLastLogin(UUID id) {
        log.info("Updating last login for employer: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", id));

        employer.setLastLogin(LocalDateTime.now());
        employerRepository.save(employer);

        log.info("Last login updated for employer: {}", id);
    }

    // ==================== Mappers ====================

    private EmployerResponse mapToResponse(Employer employer) {
        return EmployerResponse.builder()
                .id(employer.getId())
                .userId(employer.getUserId())
                .organizationId(employer.getOrganization().getId())
                .organizationName(employer.getOrganization().getCompanyName())
                .firstName(employer.getFirstName())
                .lastName(employer.getLastName())
                .fullName(employer.getFirstName() + " " + employer.getLastName())
                .email(employer.getEmail())
                .phone(employer.getPhone())
                .profilePicture(employer.getProfilePicture())
                .jobTitle(employer.getJobTitle())
                .department(employer.getDepartment())
                .role(employer.getRole() != null ? employer.getRole().toString() : null)
                .status(employer.getStatus() != null ? employer.getStatus().toString() : null)
                .isPrimaryContact(employer.getIsPrimaryContact())
                .lastLogin(employer.getLastLogin())
                .createdAt(employer.getCreatedAt())
                .updatedAt(employer.getUpdatedAt())
                .build();
    }

    private EmployerListResponse mapToListResponse(Employer employer) {
        return EmployerListResponse.builder()
                .id(employer.getId())
                .fullName(employer.getFirstName() + " " + employer.getLastName())
                .email(employer.getEmail())
                .jobTitle(employer.getJobTitle())
                .organizationName(employer.getOrganization().getCompanyName())
                .role(employer.getRole() != null ? employer.getRole().toString() : null)
                .status(employer.getStatus() != null ? employer.getStatus().toString() : null)
                .isPrimaryContact(employer.getIsPrimaryContact())
                .createdAt(employer.getCreatedAt())
                .build();
    }
}
