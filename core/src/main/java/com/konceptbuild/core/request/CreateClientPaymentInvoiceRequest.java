package com.konceptbuild.core.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateClientPaymentInvoiceRequest {
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId;

    @NotNull(message = "Paid value is required")
    Double paidValue;
}
