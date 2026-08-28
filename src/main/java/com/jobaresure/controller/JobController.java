package com.jobaresure.controller;

import com.jobaresure.dto.ApiResponse;
import com.jobaresure.dto.job.*;
import com.jobaresure.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management")
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody CreateJobRequest request) {

        JobResponse response = jobService.createJob(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", response));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobByPublicId(
            @PathVariable String publicId) {

        JobResponse response = jobService.getJobByPublicId(publicId);

        return ResponseEntity.ok(ApiResponse.success("Job retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobListResponse>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<JobListResponse> jobs = jobService.getAllJobs(pageable);

        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable String publicId,
            @Valid @RequestBody UpdateJobRequest request) {

        JobResponse response = jobService.updateJob(publicId, request);

        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", response));
    }

    @PatchMapping("/{publicId}/status")
    public ResponseEntity<ApiResponse<JobResponse>> updateStatus(
            @PathVariable String publicId,
            @RequestBody UpdateJobStatusRequest request) {

        JobResponse response = jobService.updateStatus(publicId, request.getStatus());

        return ResponseEntity.ok(ApiResponse.success("Job status updated successfully", response));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable String publicId) {

        jobService.deleteJob(publicId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Job deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<JobListResponse>>> searchJobs(
            JobSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)
        );

        Page<JobListResponse> jobs = jobService.searchJobs(criteria, pageable);

        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }
}
