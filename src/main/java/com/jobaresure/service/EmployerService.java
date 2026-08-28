package com.jobaresure.service;

import com.jobaresure.dto.employer.CreateEmployerRequest;
import com.jobaresure.dto.employer.EmployerListResponse;
import com.jobaresure.dto.employer.EmployerResponse;
import com.jobaresure.dto.employer.UpdateEmployerRequest;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.EmployerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployerService {

    // Create employer under organization
    EmployerResponse createEmployer(UUID organizationId, CreateEmployerRequest request);

    // Get single employer
    EmployerResponse getEmployerById(UUID id);

    // Get all employers (paginated)
    Page<EmployerListResponse> getAllEmployers(Pageable pageable);

    // Get employers by organization
    Page<EmployerListResponse> getEmployersByOrganization(UUID organizationId, Pageable pageable);

    // Update employer
    EmployerResponse updateEmployer(UUID id, UpdateEmployerRequest request);

    // Delete employer
    void deleteEmployer(UUID id);

    // Update status
    EmployerResponse updateStatus(UUID id, EmployerStatus status);

    // Update role
    EmployerResponse updateRole(UUID id, EmployerRole role);

    // Set primary contact
    EmployerResponse setPrimaryContact(UUID id);

    // Check email exists
    boolean existsByEmail(String email);

    // Update last login
    void updateLastLogin(UUID id);

}
