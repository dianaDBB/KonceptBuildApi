package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.ClientPaymentInvoiceEntity;
import com.konceptbuild.core.entity.ClientPaymentInvoiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ClientPaymentInvoiceRepository extends JpaRepository<ClientPaymentInvoiceEntity,
        ClientPaymentInvoiceId> {
    @Transactional
    void deleteByPayment_Id(UUID paymentId);
}
