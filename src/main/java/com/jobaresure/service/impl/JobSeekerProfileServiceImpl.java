package com.jobaresure.service.impl;

import com.jobaresure.dto.profile.*;
import com.jobaresure.entity.*;
import com.jobaresure.exception.BadRequestException;
import com.jobaresure.exception.DuplicateResourceException;
import com.jobaresure.exception.ResourceNotFoundException;
import com.jobaresure.repository.*;
import com.jobaresure.security.SecurityUtils;
import com.jobaresure.service.JobSeekerProfileService;
import com.jobaresure.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileServiceImpl implements JobSeekerProfileService {

    private final ApplicantRepository applicantRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final ApplicantSkillRepository applicantSkillRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;

    // ---- Profile ----

    @Override
    @Transactional(readOnly = true)
    public JobSeekerProfileResponse getMyProfile() {
        return toResponse(currentApplicant());
    }

    @Override
    @Transactional
    public JobSeekerProfileResponse updateMyProfile(UpdateJobSeekerProfileRequest req) {
        Applicant a = currentApplicant();

        if (req.getFirstName() != null) a.setFirstName(req.getFirstName());
        if (req.getLastName() != null) a.setLastName(req.getLastName());
        if (req.getPhone() != null) a.setPhone(req.getPhone());
        if (req.getHeadline() != null) a.setHeadline(req.getHeadline());
        if (req.getBio() != null) a.setBio(req.getBio());
        if (req.getCurrentLocation() != null) a.setCurrentLocation(req.getCurrentLocation());
        if (req.getTotalExperienceYears() != null) a.setTotalExperienceYears(req.getTotalExperienceYears());
        if (req.getCurrentSalary() != null) a.setCurrentSalary(req.getCurrentSalary());
        if (req.getExpectedSalary() != null) a.setExpectedSalary(req.getExpectedSalary());
        if (req.getCurrency() != null) a.setCurrency(req.getCurrency());
        if (req.getPortfolioUrl() != null) a.setPortfolioUrl(req.getPortfolioUrl());
        if (req.getLinkedinUrl() != null) a.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getGithubUrl() != null) a.setGithubUrl(req.getGithubUrl());
        if (req.getNoticePeriod() != null) a.setNoticePeriod(req.getNoticePeriod());
        if (req.getIsActivelyLooking() != null) a.setIsActivelyLooking(req.getIsActivelyLooking());

        recomputeCompletion(a);
        return toResponse(applicantRepository.save(a));
    }

    // ---- Work experience ----

    @Override
    @Transactional
    public WorkExperienceDto addExperience(WorkExperienceRequest req) {
        Applicant a = currentApplicant();
        WorkExperience we = workExperienceRepository.save(WorkExperience.builder()
                .applicant(a)
                .jobTitle(req.getJobTitle())
                .companyName(req.getCompanyName())
                .location(req.getLocation())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isCurrent(Boolean.TRUE.equals(req.getIsCurrent()))
                .description(req.getDescription())
                .build());
        recomputeCompletion(a);
        applicantRepository.save(a);
        return toDto(we);
    }

    @Override
    @Transactional
    public WorkExperienceDto updateExperience(UUID id, WorkExperienceRequest req) {
        Applicant a = currentApplicant();
        WorkExperience we = workExperienceRepository.findByIdAndApplicantId(id, a.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));
        we.setJobTitle(req.getJobTitle());
        we.setCompanyName(req.getCompanyName());
        we.setLocation(req.getLocation());
        we.setStartDate(req.getStartDate());
        we.setEndDate(req.getEndDate());
        we.setIsCurrent(Boolean.TRUE.equals(req.getIsCurrent()));
        we.setDescription(req.getDescription());
        return toDto(workExperienceRepository.save(we));
    }

    @Override
    @Transactional
    public void deleteExperience(UUID id) {
        Applicant a = currentApplicant();
        WorkExperience we = workExperienceRepository.findByIdAndApplicantId(id, a.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));
        workExperienceRepository.delete(we);
        recomputeCompletion(a);
        applicantRepository.save(a);
    }

    // ---- Education ----

    @Override
    @Transactional
    public EducationDto addEducation(EducationRequest req) {
        Applicant a = currentApplicant();
        Education e = educationRepository.save(Education.builder()
                .applicant(a)
                .degree(req.getDegree())
                .fieldOfStudy(req.getFieldOfStudy())
                .institutionName(req.getInstitutionName())
                .location(req.getLocation())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isCurrent(Boolean.TRUE.equals(req.getIsCurrent()))
                .grade(req.getGrade())
                .description(req.getDescription())
                .build());
        recomputeCompletion(a);
        applicantRepository.save(a);
        return toDto(e);
    }

    @Override
    @Transactional
    public EducationDto updateEducation(UUID id, EducationRequest req) {
        Applicant a = currentApplicant();
        Education e = educationRepository.findByIdAndApplicantId(id, a.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));
        e.setDegree(req.getDegree());
        e.setFieldOfStudy(req.getFieldOfStudy());
        e.setInstitutionName(req.getInstitutionName());
        e.setLocation(req.getLocation());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setIsCurrent(Boolean.TRUE.equals(req.getIsCurrent()));
        e.setGrade(req.getGrade());
        e.setDescription(req.getDescription());
        return toDto(educationRepository.save(e));
    }

    @Override
    @Transactional
    public void deleteEducation(UUID id) {
        Applicant a = currentApplicant();
        Education e = educationRepository.findByIdAndApplicantId(id, a.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));
        educationRepository.delete(e);
        recomputeCompletion(a);
        applicantRepository.save(a);
    }

    // ---- Skills ----

    @Override
    @Transactional
    public ApplicantSkillDto addSkill(AddApplicantSkillRequest req) {
        Applicant a = currentApplicant();

        Skill skill;
        if (req.hasSkillId()) {
            UUID skillId = parseUuid(req.getSkillId(), "skillId");
            skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        } else if (StringUtils.hasText(req.getSkillName())) {
            skill = skillService.getOrCreateByName(req.getSkillName());
        } else {
            throw new BadRequestException("Provide either skillId or skillName");
        }

        if (applicantSkillRepository.existsByApplicantIdAndSkillId(a.getId(), skill.getId())) {
            throw new DuplicateResourceException("Skill already added to your profile");
        }

        ApplicantSkill as = applicantSkillRepository.save(ApplicantSkill.builder()
                .applicant(a)
                .skill(skill)
                .proficiency(req.getProficiency())
                .yearsOfExperience(req.getYearsOfExperience())
                .build());

        recomputeCompletion(a);
        applicantRepository.save(a);
        return toDto(as);
    }

    @Override
    @Transactional
    public void removeSkill(UUID applicantSkillId) {
        Applicant a = currentApplicant();
        ApplicantSkill as = applicantSkillRepository.findByIdAndApplicantId(applicantSkillId, a.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found on your profile"));
        applicantSkillRepository.delete(as);
        recomputeCompletion(a);
        applicantRepository.save(a);
    }

    // ---- Helpers ----

    private Applicant currentApplicant() {
        UUID userId = SecurityUtils.getCurrentUser().getId();
        return applicantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));
    }

    private UUID parseUuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid " + field);
        }
    }

    private void recomputeCompletion(Applicant a) {
        int score = 0;
        if (StringUtils.hasText(a.getFirstName()) && StringUtils.hasText(a.getLastName())
                && StringUtils.hasText(a.getPhone())) score += 15;
        if (StringUtils.hasText(a.getHeadline())) score += 10;
        if (StringUtils.hasText(a.getBio())) score += 10;
        if (StringUtils.hasText(a.getCurrentLocation())) score += 10;
        if (a.getTotalExperienceYears() != null) score += 5;
        if (StringUtils.hasText(a.getResumeUrl())) score += 15;
        if (!workExperienceRepository.findByApplicantIdOrderByStartDateDesc(a.getId()).isEmpty()) score += 15;
        if (!educationRepository.findByApplicantIdOrderByStartDateDesc(a.getId()).isEmpty()) score += 10;
        if (!applicantSkillRepository.findByApplicantId(a.getId()).isEmpty()) score += 10;
        a.setProfileCompleted(Math.min(score, 100));
    }

    private JobSeekerProfileResponse toResponse(Applicant a) {
        List<WorkExperienceDto> experiences = workExperienceRepository
                .findByApplicantIdOrderByStartDateDesc(a.getId()).stream().map(this::toDto).toList();
        List<EducationDto> educations = educationRepository
                .findByApplicantIdOrderByStartDateDesc(a.getId()).stream().map(this::toDto).toList();
        List<ApplicantSkillDto> skills = applicantSkillRepository
                .findByApplicantId(a.getId()).stream().map(this::toDto).toList();

        return JobSeekerProfileResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .email(a.getEmail())
                .phone(a.getPhone())
                .profilePicture(a.getProfilePicture())
                .headline(a.getHeadline())
                .bio(a.getBio())
                .currentLocation(a.getCurrentLocation())
                .totalExperienceYears(a.getTotalExperienceYears())
                .currentSalary(a.getCurrentSalary())
                .expectedSalary(a.getExpectedSalary())
                .currency(a.getCurrency())
                .resumeUrl(a.getResumeUrl())
                .portfolioUrl(a.getPortfolioUrl())
                .linkedinUrl(a.getLinkedinUrl())
                .githubUrl(a.getGithubUrl())
                .noticePeriod(a.getNoticePeriod())
                .isActivelyLooking(a.getIsActivelyLooking())
                .profileCompleted(a.getProfileCompleted())
                .identityVerified(a.getIdentityVerified())
                .status(a.getStatus())
                .workExperiences(experiences)
                .educations(educations)
                .skills(skills)
                .build();
    }

    private WorkExperienceDto toDto(WorkExperience we) {
        return WorkExperienceDto.builder()
                .id(we.getId())
                .jobTitle(we.getJobTitle())
                .companyName(we.getCompanyName())
                .location(we.getLocation())
                .startDate(we.getStartDate())
                .endDate(we.getEndDate())
                .isCurrent(we.getIsCurrent())
                .description(we.getDescription())
                .build();
    }

    private EducationDto toDto(Education e) {
        return EducationDto.builder()
                .id(e.getId())
                .degree(e.getDegree())
                .fieldOfStudy(e.getFieldOfStudy())
                .institutionName(e.getInstitutionName())
                .location(e.getLocation())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .isCurrent(e.getIsCurrent())
                .grade(e.getGrade())
                .description(e.getDescription())
                .build();
    }

    private ApplicantSkillDto toDto(ApplicantSkill as) {
        return ApplicantSkillDto.builder()
                .id(as.getId())
                .skillId(as.getSkill().getId())
                .skillName(as.getSkill().getName())
                .category(as.getSkill().getCategory())
                .proficiency(as.getProficiency())
                .yearsOfExperience(as.getYearsOfExperience())
                .build();
    }
}
