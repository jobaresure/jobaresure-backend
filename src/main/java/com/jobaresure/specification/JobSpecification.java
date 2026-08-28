package com.jobaresure.specification;

import com.jobaresure.dto.job.JobSearchCriteria;
import com.jobaresure.entity.Job;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<Job> filterBy(JobSearchCriteria criteria) {

        return (Root<Job> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Keyword search (title + description)
            if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
                String pattern = "%" + criteria.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("jobTitle")), pattern),
                                cb.like(cb.lower(root.get("jobDescription")), pattern)
                        )
                );
            }

            if (criteria.getLocation() != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("location")),
                                "%" + criteria.getLocation().toLowerCase() + "%"
                        )
                );
            }

            if (criteria.getJobType() != null) {
                predicates.add(cb.equal(root.get("jobType"), criteria.getJobType()));
            }

            if (criteria.getWorkMode() != null) {
                predicates.add(cb.equal(root.get("workMode"), criteria.getWorkMode()));
            }

            if (criteria.getSalaryMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("salaryMin"), criteria.getSalaryMin()));
            }

            if (criteria.getSalaryMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("salaryMax"), criteria.getSalaryMax()));
            }

            if (criteria.getExperienceMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("experienceMin"), criteria.getExperienceMin()));
            }

            if (criteria.getExperienceMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("experienceMax"), criteria.getExperienceMax()));
            }

            if (criteria.getSeniorityLevel() != null) {
                predicates.add(cb.equal(root.get("seniorityLevel"), criteria.getSeniorityLevel()));
            }

            if (criteria.getEducationLevel() != null) {
                predicates.add(cb.equal(root.get("educationLevel"), criteria.getEducationLevel()));
            }

            if (criteria.getIsFeatured() != null) {
                predicates.add(cb.equal(root.get("isFeatured"), criteria.getIsFeatured()));
            }

            if (criteria.getIsUrgent() != null) {
                predicates.add(cb.equal(root.get("isUrgent"), criteria.getIsUrgent()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getOrganizationPublicId() != null) {
                Join<Object, Object> orgJoin = root.join("organization");
                predicates.add(cb.equal(orgJoin.get("publicId"),
                        criteria.getOrganizationPublicId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
