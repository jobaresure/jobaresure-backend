package com.jobaresure.service.impl;

import com.jobaresure.dto.verification.ReviewVerificationRequest;
import com.jobaresure.dto.verification.SubmitVerificationRequest;
import com.jobaresure.dto.verification.VerificationResponse;
import com.jobaresure.entity.Applicant;
import com.jobaresure.entity.Employer;
import com.jobaresure.entity.Organization;
import com.jobaresure.entity.User;
import com.jobaresure.entity.VerificationRecord;
import com.jobaresure.enums.DocumentType;
import com.jobaresure.enums.EmployerRole;
import com.jobaresure.enums.VerificationStatus;
import com.jobaresure.enums.VerificationType;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.ApplicantRepository;
import com.jobaresure.repository.EmployerRepository;
import com.jobaresure.repository.OrganizationRepository;
import com.jobaresure.repository.VerificationRecordRepository;
import com.jobaresure.security.SecurityUtils;
import com.jobaresure.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private static final Set<DocumentType> IDENTITY_DOCS = EnumSet.of(
            DocumentType.AADHAAR, DocumentType.PAN, DocumentType.DRIVING_LICENSE,
            DocumentType.VOTER_ID, DocumentType.PASSPORT);

    private static final Set<DocumentType> COMPANY_DOCS = EnumSet.of(
            DocumentType.GST_CERTIFICATE, DocumentType.INCORPORATION_CERTIFICATE,
            DocumentType.BUSINESS_PAN, DocumentType.UDYAM_CERTIFICATE, DocumentType.OTHER);

    private final VerificationRecordRepository verificationRepository;
    private final ApplicantRepository applicantRepository;
    private final EmployerRepository employerRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public VerificationResponse submitIdentity(SubmitVerificationRequest req) {
        if (!IDENTITY_DOCS.contains(req.getDocumentType())) {
            throw new BadRequestException("Invalid identity document type");
        }
        User user = SecurityUtils.getCurrentUser();
        Applicant applicant = applicantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));

        guardNoPending(VerificationType.IDENTITY, applicant.getId());

        VerificationRecord record = verificationRepository.save(VerificationRecord.builder()
                .type(VerificationType.IDENTITY)
                .subjectId(applicant.getId())
                .submittedByUserId(user.getId())
                .documentType(req.getDocumentType())
                .documentUrl(req.getDocumentUrl())
                .documentNumberMasked(mask(req.getDocumentNumber()))
                .status(VerificationStatus.PENDING)
                .build());

        return VerificationResponse.from(record);
    }

    @Override
    @Transactional
    public VerificationResponse submitCompany(SubmitVerificationRequest req) {
        if (!COMPANY_DOCS.contains(req.getDocumentType())) {
            throw new BadRequestException("Invalid company document type");
        }
        User user = SecurityUtils.getCurrentUser();
        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));
        if (employer.getRole() != EmployerRole.admin) {
            throw new AccessDeniedException("Only a company admin can submit company verification");
        }
        Organization org = employer.getOrganization();

        guardNoPending(VerificationType.COMPANY, org.getId());

        VerificationRecord record = verificationRepository.save(VerificationRecord.builder()
                .type(VerificationType.COMPANY)
                .subjectId(org.getId())
                .submittedByUserId(user.getId())
                .documentType(req.getDocumentType())
                .documentUrl(req.getDocumentUrl())
                .documentNumberMasked(mask(req.getDocumentNumber()))
                .status(VerificationStatus.PENDING)
                .build());

        return VerificationResponse.from(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationResponse> getMyVerifications() {
        UUID userId = SecurityUtils.getCurrentUser().getId();
        return verificationRepository.findBySubmittedByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(VerificationResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VerificationResponse> adminList(VerificationStatus status, Pageable pageable) {
        Page<VerificationRecord> page = (status != null)
                ? verificationRepository.findByStatus(status, pageable)
                : verificationRepository.findAll(pageable);
        return page.map(VerificationResponse::from);
    }

    @Override
    @Transactional
    public VerificationResponse adminReview(UUID verificationId, ReviewVerificationRequest req) {
        VerificationStatus decision = req.getDecision();
        if (decision == VerificationStatus.PENDING) {
            throw new BadRequestException("Decision must be APPROVED, REJECTED, or RESUBMIT_REQUIRED");
        }

        VerificationRecord record = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification record not found"));

        record.setStatus(decision);
        record.setReviewNotes(req.getReviewNotes());
        record.setReviewedByUserId(SecurityUtils.getCurrentUser().getId());
        record.setReviewedAt(LocalDateTime.now());

        if (decision == VerificationStatus.APPROVED) {
            applyApproval(record);
        }

        return VerificationResponse.from(verificationRepository.save(record));
    }

    // ---- Helpers ----

    private void applyApproval(VerificationRecord record) {
        if (record.getType() == VerificationType.IDENTITY) {
            applicantRepository.findById(record.getSubjectId()).ifPresent(a -> {
                a.setIdentityVerified(true);
                applicantRepository.save(a);
            });
        } else if (record.getType() == VerificationType.COMPANY) {
            organizationRepository.findById(record.getSubjectId()).ifPresent(o -> {
                o.setIsVerified(true);
                organizationRepository.save(o);
            });
        }
    }

    private void guardNoPending(VerificationType type, UUID subjectId) {
        if (verificationRepository.existsByTypeAndSubjectIdAndStatus(type, subjectId, VerificationStatus.PENDING)) {
            throw new BadRequestException("A verification request is already pending review");
        }
    }

    /** Keep only the last 4 characters visible, mask the rest. */
    private String mask(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= 4) {
            return "X".repeat(trimmed.length());
        }
        return "X".repeat(trimmed.length() - 4) + trimmed.substring(trimmed.length() - 4);
    }
}
