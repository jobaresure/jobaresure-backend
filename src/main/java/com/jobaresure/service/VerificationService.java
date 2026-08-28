package com.jobaresure.service;

import com.jobaresure.dto.verification.ReviewVerificationRequest;
import com.jobaresure.dto.verification.SubmitVerificationRequest;
import com.jobaresure.dto.verification.VerificationResponse;
import com.jobaresure.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VerificationService {

    VerificationResponse submitIdentity(SubmitVerificationRequest request);

    VerificationResponse submitCompany(SubmitVerificationRequest request);

    List<VerificationResponse> getMyVerifications();

    Page<VerificationResponse> adminList(VerificationStatus status, Pageable pageable);

    VerificationResponse adminReview(UUID verificationId, ReviewVerificationRequest request);
}
