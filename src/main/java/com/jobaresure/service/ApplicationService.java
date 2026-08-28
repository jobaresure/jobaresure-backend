package com.jobaresure.service;

import com.jobaresure.dto.ApplicationResponse;
import com.jobaresure.dto.ApplyJobRequest;
import com.jobaresure.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    /** Job seeker applies to a job (applicant derived from the authenticated user). */
    ApplicationResponse apply(ApplyJobRequest request);

    /** Job seeker lists their own applications, newest first. */
    List<ApplicationResponse> getMyApplications();

    /** Job seeker withdraws one of their own applications. */
    ApplicationResponse withdraw(UUID applicationId);

    /** Company lists the applications for one of its jobs (by public id). */
    Page<ApplicationResponse> getApplicationsForJob(String jobPublicId, Pageable pageable);

    /** Company moves an application through its hiring pipeline. */
    ApplicationResponse updateStatus(UUID applicationId, ApplicationStatus status);
}
