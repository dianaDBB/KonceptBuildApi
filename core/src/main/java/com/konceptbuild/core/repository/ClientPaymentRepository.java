package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.ClientPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientPaymentRepository extends JpaRepository<ClientPaymentEntity, UUID> {
}
