package com.jobaresure.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCompanyRequest {

    // Primary contact (the person creating the account)
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone must be a valid number (E.164 format)")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String password;

    // Company
    @NotBlank(message = "Company name is required")
    @Size(max = 255)
    private String companyName;

    @NotBlank(message = "Company email is required")
    @Email(message = "Company email must be valid")
    private String companyEmail;
}
