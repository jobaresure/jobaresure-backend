package com.jobaresure.service.impl;

import com.jobaresure.dto.job.*;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Job;
import com.jobaresure.entity.Organization;
import com.jobaresure.enums.job.JobStatus;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.JobRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.service.JobService;
import com.jobaresure.specification.JobSpecification;
import com.jobaresure.util.PublicIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployerRepository employerRepository;

    @Override
    public JobResponse createJob(CreateJobRequest request) {

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));

        Employer employer = employerRepository.findById(request.getPostedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", request.getPostedBy()));

        // Ensure employer belongs to organization
        if (!employer.getOrganization().getId().equals(organization.getId())) {
            throw new BadRequestException("Employer does not belong to this organization");
        }

        String publicId;
        do {
            publicId = PublicIdGenerator.generate("JOB_");
        } while (jobRepository.existsByPublicId(publicId));

        Job job = Job.builder()
                .publicId(publicId)
                .jobTitle(request.getJobTitle())
                .organization(organization)
                .postedBy(employer)
                .jobType(request.getJobType())
                .workMode(request.getWorkMode())
                .location(request.getLocation())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .experienceMin(request.getExperienceMin())
                .experienceMax(request.getExperienceMax())
                .applicationDeadline(request.getApplicationDeadline())
                .jobDescription(request.getJobDescription())
                .status(JobStatus.draft)
                .build();

        Job saved = jobRepository.save(job);

        return mapToResponse(saved);
    }

    @Override
//    @Transactional(readOnly = true)
    public JobResponse getJobByPublicId(String publicId) {

        Job job = jobRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "publicId", publicId));

        return mapToResponse(job);
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<JobListResponse> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable)
                .map(this::mapToListResponse);
    }

    @Override
    public JobResponse updateJob(String publicId, UpdateJobRequest request) {

        Job job = jobRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "publicId", publicId));

        if (request.getJobTitle() != null) job.setJobTitle(request.getJobTitle());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getSalaryMin() != null) job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null) job.setSalaryMax(request.getSalaryMax());
        if (request.getExperienceMin() != null) job.setExperienceMin(request.getExperienceMin());
        if (request.getExperienceMax() != null) job.setExperienceMax(request.getExperienceMax());
        if (request.getJobDescription() != null) job.setJobDescription(request.getJobDescription());
        if (request.getIsFeatured() != null) job.setIsFeatured(request.getIsFeatured());
        if (request.getIsUrgent() != null) job.setIsUrgent(request.getIsUrgent());

        Job updated = jobRepository.save(job);

        return mapToResponse(updated);
    }

    @Override
    public JobResponse updateStatus(String publicId, JobStatus status) {

        Job job = jobRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "publicId", publicId));

        job.setStatus(status);

        if (status == JobStatus.active) {
            job.setPublishedAt(LocalDateTime.now());
        }

        if (status == JobStatus.closed) {
            job.setClosedAt(LocalDateTime.now());
        }

        Job updated = jobRepository.save(job);

        return mapToResponse(updated);
    }

    @Override
    public void deleteJob(String publicId) {

        Job job = jobRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "publicId", publicId));

        jobRepository.delete(job);
    }

    @Override
//    @Transactional(readOnly = true)
    @Transactional
    public Page<JobListResponse> searchJobs(JobSearchCriteria criteria, Pageable pageable) {

        Specification<Job> specification = JobSpecification.filterBy(criteria);

        return jobRepository.findAll(specification, pageable).map(this::mapToListResponse);
    }


        // ==================== MAPPERS ====================

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .publicId(job.getPublicId())
                .jobTitle(job.getJobTitle())
                .organizationPublicId(job.getOrganization().getPublicId())
                .organizationName(job.getOrganization().getCompanyName())
                .postedByPublicId(job.getPublicId())
                .postedByName(job.getPostedBy().getFirstName() + " " + job.getPostedBy().getLastName())
                .jobType(job.getJobType())
                .workMode(job.getWorkMode())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryCurrency(job.getSalaryCurrency())
                .jobDescription(job.getJobDescription())
                .status(job.getStatus())
                .isFeatured(job.getIsFeatured())
                .isUrgent(job.getIsUrgent())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .publishedAt(job.getPublishedAt())
                .closedAt(job.getClosedAt())
                .build();
    }

    private JobListResponse mapToListResponse(Job job) {
        return JobListResponse.builder()
                .publicId(job.getPublicId())
                .jobTitle(job.getJobTitle())
                .organizationName(job.getOrganization().getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .workMode(job.getWorkMode())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .isFeatured(job.getIsFeatured())
                .isUrgent(job.getIsUrgent())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
