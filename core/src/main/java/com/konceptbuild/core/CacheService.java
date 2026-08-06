package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface CacheService {
    void refreshCache();

    List<WorkerDto> getAllWorkers();

    List<WorkerDto> getAllActiveWorkers(Integer year, Integer month);

    Optional<WorkerDto> getWorkerById(UUID id);

    List<ClientDto> getAllClients();

    Optional<ClientDto> getClientById(UUID clientId);

    List<WorkDto> getAllWorks();

    Optional<WorkDto> getWorkById(UUID id);

    List<WageDto> getAllWages();

    Optional<WorkerHistoryDto> getWorkerHistory(UUID workerId, Integer year, Integer month);

    List<ClientInvoiceDto> getAllClientInvoices();

    Optional<ClientInvoiceDto> getClientInvoiceById(UUID invoiceId);

    Optional<ClientInvoiceDto> getClientInvoiceByDocNumber(String docNumber);

    List<ClientCreditNoteDto> getAllClientCreditNotes();

    Optional<ClientCreditNoteDto> getClientCreditNoteById(UUID creditNoteId);

    Optional<ClientCreditNoteDto> getClientCreditNoteByDocNumber(String docNumber);

    List<ClientPaymentDto> getAllClientPayments();

    Optional<ClientPaymentDto> getClientPaymentById(UUID paymentID);

    List<ClientPaymentDto> getClientPaymentByInvoice(UUID invoiceId);
}