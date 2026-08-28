package com.jobaresure.service;

import com.jobaresure.dto.profile.CompanyProfileResponse;
import com.jobaresure.dto.profile.EmployerProfileDto;
import com.jobaresure.dto.profile.UpdateCompanyProfileRequest;
import com.jobaresure.dto.profile.UpdateEmployerProfileRequest;

public interface CompanyProfileService {

    CompanyProfileResponse getMyCompany();

    CompanyProfileResponse updateMyCompany(UpdateCompanyProfileRequest request);

    EmployerProfileDto getMyEmployer();

    EmployerProfileDto updateMyEmployer(UpdateEmployerProfileRequest request);
}
