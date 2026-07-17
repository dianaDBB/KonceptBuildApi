package com.konceptbuild.core.request;

import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.validator.ValidClient;
import jakarta.validation.constraints.*;

import java.util.UUID;

@ValidClient
public record ClientRequest(
        UUID id,

        @NotBlank(message = "Company name is required")
        String companyName,

        String address,

        @Pattern(
                regexp = "^\\d{4}-\\d{3}$",
                message = "Postal code must be in the format xxxx-xxx"
        )
        String postalCode,

        String city,

        String district,

        @NotBlank(message = "NIF is required")
        String nif,

        @NotBlank(message = "Contact is required")
        String contact,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email must be a valid email address"
        )
        String email,

        @NotBlank(message = "Phone country code is required")
        @Pattern(
                regexp = "^\\+[1-9]\\d{0,2}$",
                message = "Invalid country code"
        )
        String phoneCountryCode,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^\\d{9}$",
                message = "Phone number must contain exactly 9 digits"
        )
        String phone,

        @NotNull(message = "Status is required")
        Status status,

        String note
) {
}
