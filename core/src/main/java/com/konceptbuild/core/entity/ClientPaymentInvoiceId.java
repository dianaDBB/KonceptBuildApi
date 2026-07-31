package com.konceptbuild.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ClientPaymentInvoiceId implements Serializable {

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "invoice_id")
    private UUID invoiceId;
}
