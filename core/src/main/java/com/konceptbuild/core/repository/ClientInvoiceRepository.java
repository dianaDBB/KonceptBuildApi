package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.ClientInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientInvoiceRepository extends JpaRepository<ClientInvoiceEntity, UUID> {
}
