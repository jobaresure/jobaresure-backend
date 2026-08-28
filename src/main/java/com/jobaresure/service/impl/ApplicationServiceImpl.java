package com.jobaresure.service.impl;

import com.jobaresure.dto.ApplicationResponse;
import com.jobaresure.dto.ApplyJobRequest;
import com.jobaresure.entity.Applicant;
import com.jobaresure.entity.Application;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Job;
import com.jobaresure.entity.User;
import com.jobaresure.enums.ApplicationStatus;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.ApplicantRepository;
import com.jobaresure.repository.ApplicationRepository;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.JobRepository;
import com.jobaresure.security.SecurityUtils;
import com.jobaresure.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    /** Statuses a company may set while moving an application through its pipeline. */
    private static final Set<ApplicationStatus> COMPANY_SETTABLE = EnumSet.of(
            ApplicationStatus.reviewing, ApplicationStatus.shortlisted,
            ApplicationStatus.interview, ApplicationStatus.offered,
            ApplicationStatus.rejected);

    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public ApplicationResponse apply(ApplyJobRequest request) {
        Applicant applicant = currentApplicant();

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", request.getJobId()));

        if (job.getStatus() != JobStatus.active) {
            throw new BadRequestException("This job is not open for applications");
        }
        if (applicationRepository.existsByJobIdAndApplicantId(job.getId(), applicant.getId())) {
            throw new DuplicateResourceException("You have already applied to this job");
        }

        String resumeUrl = StringUtils.hasText(request.getResumeUrl())
                ? request.getResumeUrl()
                : applicant.getResumeUrl();

        Application application = applicationRepository.save(Application.builder()
                .job(job)
                .applicant(applicant)
                .resumeUrl(resumeUrl)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.applied)
                .build());

        job.setTotalApplications(job.getTotalApplications() + 1);
        jobRepository.save(job);

        return ApplicationResponse.from(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications() {
        Applicant applicant = currentApplicant();
        return applicationRepository.findByApplicantIdOrderByCreatedAtDesc(applicant.getId())
                .stream().map(ApplicationResponse::from).toList();
    }

    @Override
    @Transactional
    public ApplicationResponse withdraw(UUID applicationId) {
        Applicant applicant = currentApplicant();
        Application application = applicationRepository
                .findByIdAndApplicantId(applicationId, applicant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() == ApplicationStatus.withdrawn) {
            throw new BadRequestException("Application is already withdrawn");
        }
        application.setStatus(ApplicationStatus.withdrawn);
        return ApplicationResponse.from(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForJob(String jobPublicId, Pageable pageable) {
        Job job = jobRepository.findByPublicId(jobPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "publicId", jobPublicId));
        guardOwnsJob(job);
        return applicationRepository.findByJobId(job.getId(), pageable)
                .map(ApplicationResponse::from);
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(UUID applicationId, ApplicationStatus status) {
        if (!COMPANY_SETTABLE.contains(status)) {
            throw new BadRequestException("Status must be one of " + COMPANY_SETTABLE);
        }
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        guardOwnsJob(application.getJob());

        if (application.getStatus() == ApplicationStatus.withdrawn) {
            throw new BadRequestException("Cannot update a withdrawn application");
        }
        application.setStatus(status);
        return ApplicationResponse.from(applicationRepository.save(application));
    }

    // ---- Helpers ----

    private Applicant currentApplicant() {
        User user = SecurityUtils.getCurrentUser();
        return applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));
    }

    /** Ensures the current employer belongs to the organization that owns the job. */
    private void guardOwnsJob(Job job) {
        User user = SecurityUtils.getCurrentUser();
        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));
        if (!employer.getOrganization().getId().equals(job.getOrganization().getId())) {
            throw new AccessDeniedException("This job belongs to another organization");
        }
    }
}
