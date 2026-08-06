package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientInvoiceDto;
import com.konceptbuild.core.filter.ClientInvoiceFilter;
import com.konceptbuild.core.request.CreateClientCreditNoteRequest;
import com.konceptbuild.core.request.CreateClientInvoiceRequest;
import com.konceptbuild.core.request.UpdateClientCreditNoteRequest;
import com.konceptbuild.core.request.UpdateClientInvoiceRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ClientInvoiceService {
    List<ClientInvoiceDto> search(ClientInvoiceFilter filter);

    void add(CreateClientInvoiceRequest request);

    void update(UpdateClientInvoiceRequest request);

    void delete(UUID id);

    void addCreditNote(UUID invoiceId, CreateClientCreditNoteRequest request);

    void updateCreditNote(UUID invoiceId, UpdateClientCreditNoteRequest request);

    void deleteCreditNote(UUID invoiceId, UUID creditNoteId);
}
