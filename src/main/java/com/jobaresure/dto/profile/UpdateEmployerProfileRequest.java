package com.jobaresure.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

/** Partial update of the current user's employer record. Null fields are unchanged. */
@Data
public class UpdateEmployerProfileRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    private String profilePicture;

    @Size(max = 150)
    private String jobTitle;

    @Size(max = 100)
    private String department;
}
