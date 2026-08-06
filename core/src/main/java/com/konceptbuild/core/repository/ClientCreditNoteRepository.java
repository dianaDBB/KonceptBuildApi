package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.ClientCreditNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientCreditNoteRepository extends JpaRepository<ClientCreditNoteEntity, UUID> {
    Optional<ClientCreditNoteEntity> findByIdAndClientInvoiceId(UUID id, UUID clientInvoiceId);
}
