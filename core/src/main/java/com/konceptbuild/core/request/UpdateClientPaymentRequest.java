package com.konceptbuild.core.request;

import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.enums.ClientPaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateClientPaymentRequest {
    @NotNull(message = "ID is required")
    UUID id;

    @NotNull(message = "Type is required")
    ClientPaymentType type;

    @NotNull(message = "Client ID is required")
    UUID clientId;

    @NotNull(message = "Payment date is required")
    LocalDate paymentDate;

    @NotNull(message = "Paid value is required")
    Double paidValue;

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod;

    private String notes;

    @NotEmpty(message = "At least 1 invoice is required")
    private List<CreateClientPaymentInvoiceRequest> invoices;
}
