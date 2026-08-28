package com.jobaresure.service;

import com.jobaresure.dto.job.*;
import com.jobaresure.enums.job.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    JobResponse createJob(CreateJobRequest request);

    JobResponse getJobByPublicId(String publicId);

    Page<JobListResponse> getAllJobs(Pageable pageable);

    JobResponse updateJob(String publicId, UpdateJobRequest request);

    JobResponse updateStatus(String publicId, JobStatus status);

    void deleteJob(String publicId);

    Page<JobListResponse> searchJobs(JobSearchCriteria criteria, Pageable pageable);
}
