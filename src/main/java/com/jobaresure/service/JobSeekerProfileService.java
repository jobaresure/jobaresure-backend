package com.jobaresure.service;

import com.jobaresure.dto.profile.*;

import java.util.UUID;

public interface JobSeekerProfileService {

    JobSeekerProfileResponse getMyProfile();

    JobSeekerProfileResponse updateMyProfile(UpdateJobSeekerProfileRequest request);

    WorkExperienceDto addExperience(WorkExperienceRequest request);

    WorkExperienceDto updateExperience(UUID id, WorkExperienceRequest request);

    void deleteExperience(UUID id);

    EducationDto addEducation(EducationRequest request);

    EducationDto updateEducation(UUID id, EducationRequest request);

    void deleteEducation(UUID id);

    ApplicantSkillDto addSkill(AddApplicantSkillRequest request);

    void removeSkill(UUID applicantSkillId);
}
