package com.konceptbuild.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client_payment_invoice")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPaymentInvoiceEntity {

    @EmbeddedId
    private ClientPaymentInvoiceId id;

    @MapsId("paymentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private ClientPaymentEntity payment;

    @MapsId("invoiceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private ClientInvoiceEntity invoice;
}
