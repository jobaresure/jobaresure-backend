package com.jobaresure.service;

import com.jobaresure.dto.organization.CreateOrganizationRequest;
import com.jobaresure.dto.organization.OrganizationListResponse;
import com.jobaresure.dto.organization.OrganizationResponse;
import com.jobaresure.dto.organization.UpdateOrganizationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrganizationService {

    // Create new organization
    OrganizationResponse createOrganization(CreateOrganizationRequest request);

    // Get organization by ID
    OrganizationResponse getOrganizationById(UUID id);

    // Get all organizations with pagination
    Page<OrganizationListResponse> getAllOrganizations(Pageable pageable);

    // Update organization
    OrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request);

    // Delete organization
    void deleteOrganization(UUID id);

    // Verify organization
    OrganizationResponse verifyOrganization(UUID id);

    // Check if organization exists
    boolean existsByCompanyName(String companyName);
}
