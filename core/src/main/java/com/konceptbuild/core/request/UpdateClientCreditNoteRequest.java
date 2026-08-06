package com.konceptbuild.core.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateClientCreditNoteRequest {
    @NotNull(message = "ID is required")
    UUID id;

    @NotBlank(message = "Document number is required")
    private String docNumber;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Value without tax value is required")
    private Double valueWithoutTax;

    @NotNull(message = "Applied tax value is required")
    private Double appliedTax;

    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    private LocalDate dueDate;
}
