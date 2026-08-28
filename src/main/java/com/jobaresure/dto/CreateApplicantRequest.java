package com.jobaresure.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateApplicantRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String phone;
    private String headline;
    private String currentLocation;
}