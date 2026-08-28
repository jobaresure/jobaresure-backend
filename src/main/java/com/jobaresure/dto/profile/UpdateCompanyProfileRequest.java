package com.jobaresure.dto.profile;

import com.jobaresure.enums.CompanySize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Partial update of the current user's organization. Null fields are unchanged. */
@Data
public class UpdateCompanyProfileRequest {

    @Size(max = 255)
    private String companyName;

    @Email(message = "Company email must be valid")
    private String companyEmail;

    private String companyPhone;
    private String logoUrl;
    private String website;
    private String industry;
    private CompanySize companySize;

    @Size(max = 500)
    private String description;

    private String about;
    private String headquartersLocation;
    private Integer foundedYear;
    private String linkedinUrl;
    private String twitterUrl;
}
