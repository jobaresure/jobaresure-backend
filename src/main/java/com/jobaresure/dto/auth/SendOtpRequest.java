package com.jobaresure.dto.auth;

import com.jobaresure.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone must be a valid number (E.164 format)")
    private String phone;

    @NotNull(message = "Purpose is required")
    private OtpPurpose purpose;
}
