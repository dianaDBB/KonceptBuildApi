package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.filter.ClientInvoiceFilter;
import com.konceptbuild.core.repository.ClientCreditNoteRepository;
import com.konceptbuild.core.repository.ClientInvoiceRepository;
import com.konceptbuild.core.request.CreateClientCreditNoteRequest;
import com.konceptbuild.core.request.CreateClientInvoiceRequest;
import com.konceptbuild.core.request.UpdateClientCreditNoteRequest;
import com.konceptbuild.core.request.UpdateClientInvoiceRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClientInvoiceServiceImpl implements ClientInvoiceService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ClientInvoiceRepository clientInvoiceRepository;

    @Autowired
    private ClientCreditNoteRepository clientCreditNoteRepository;

    @Override
    public List<ClientInvoiceDto> search(ClientInvoiceFilter filter) {
        Comparator<ClientInvoiceDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                ClientInvoiceDto.class
        );

        return cacheService.getAllClientInvoices().stream()
                .filter(invoice -> FilterHelper.matchesString(invoice.getDocNumber(), filter.docNumber()))
                .filter(invoice -> FilterHelper.matchesString(
                        List.of(
                                invoice.getClient().getCode(),
                                invoice.getClient().getCompanyName(),
                                invoice.getClient().getNif(),
                                invoice.getClient().getContact(),
                                invoice.getClient().getEmail(),
                                invoice.getClient().getPhone()
                        ),
                        filter.client()
                ))
                .filter(invoice -> FilterHelper.matchesString(
                        List.of(
                                invoice.getWork().getCode(),
                                invoice.getWork().getName()
                        ),
                        filter.work()
                ))
                .filter(invoice -> FilterHelper.matchesString(invoice.getDescription(), filter.description()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getValueWithoutTax(), filter.valueWithoutTax()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getAppliedTax(), filter.appliedTax()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getTaxValue(), filter.taxValue()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getTotalValue(), filter.totalValue()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getRegistrationDate(), filter.registrationDate()))
                .filter(invoice -> FilterHelper.isWithinRange(invoice.getDueDate(), filter.dueDate()))
                .sorted(comparator)
                .toList();
    }

    @Transactional
    @Override
    public void add(CreateClientInvoiceRequest request) {
        ClientDto clientDto = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        WorkDto workDto = cacheService.getWorkById(request.getWorkId())
                .orElseThrow(() -> new EntityNotFoundException("Work not found - " + request.getWorkId()));

        if (workDto.getClient().getId() != clientDto.getId()) {
            throw new EntityNotFoundException("Work " + workDto.getCode() + " does not belong to the selected client "
                    + clientDto.getCode());
        }

        Double taxValue = getTaxValue(request.getValueWithoutTax(), request.getAppliedTax());
        Double totalValue = getTotalValue(request.getValueWithoutTax(), taxValue);

        ClientInvoiceEntity invoiceEntity = ClientInvoiceEntity
                .builder()
                .docNumber(request.getDocNumber())
                .client(new ClientEntity(clientDto))
                .work(new WorkEntity(workDto))
                .description(request.getDescription())
                .valueWithoutTax(request.getValueWithoutTax())
                .appliedTax(request.getAppliedTax())
                .taxValue(taxValue)
                .totalValue(totalValue)
                .registrationDate(request.getRegistrationDate())
                .dueDate(request.getDueDate())
                .build();

        clientInvoiceRepository.save(invoiceEntity);
        cacheService.refreshCache();
    }

    @Override
    public void update(UpdateClientInvoiceRequest request) {
        cacheService.getClientInvoiceById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + request.getId()));

        ClientDto clientDto = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        WorkDto workDto = cacheService.getWorkById(request.getWorkId())
                .orElseThrow(() -> new EntityNotFoundException("Work not found - " + request.getWorkId()));

        if (workDto.getClient().getId() != clientDto.getId()) {
            throw new EntityNotFoundException("Work " + workDto.getCode() + " does not belong to the selected client "
                    + clientDto.getCode());
        }

        Double taxValue = getTaxValue(request.getValueWithoutTax(), request.getAppliedTax());
        Double totalValue = getTotalValue(request.getValueWithoutTax(), taxValue);

        ClientInvoiceEntity invoiceEntity = ClientInvoiceEntity
                .builder()
                .docNumber(request.getDocNumber())
                .client(new ClientEntity(clientDto))
                .work(new WorkEntity(workDto))
                .description(request.getDescription())
                .valueWithoutTax(request.getValueWithoutTax())
                .appliedTax(request.getAppliedTax())
                .taxValue(taxValue)
                .totalValue(totalValue)
                .registrationDate(request.getRegistrationDate())
                .dueDate(request.getDueDate())
                .build();

        clientInvoiceRepository.save(invoiceEntity);
        cacheService.refreshCache();
    }

    @Override
    public void delete(UUID id) {
        ClientInvoiceEntity invoice = clientInvoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + id));

        clientInvoiceRepository.delete(invoice);
        cacheService.refreshCache();
    }

    private Double getTaxValue(Double valueWithoutTax, Double appliedTax) {
        return valueWithoutTax * (appliedTax / 100);
    }

    private Double getTotalValue(Double valueWithoutTax, Double taxValue) {
        return valueWithoutTax + taxValue;
    }

    @Transactional
    @Override
    public void addCreditNote(UUID invoiceId, CreateClientCreditNoteRequest request) {
        ClientInvoiceDto invoiceDto = cacheService.getClientInvoiceById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceId));

        Double taxValue = getTaxValue(request.getValueWithoutTax(), request.getAppliedTax());
        Double totalValue = getTotalValue(request.getValueWithoutTax(), taxValue);

        ClientCreditNoteEntity creditNote = ClientCreditNoteEntity.builder()
                .clientInvoice(new ClientInvoiceEntity(invoiceDto))
                .docNumber(request.getDocNumber())
                .description(request.getDescription())
                .valueWithoutTax(request.getValueWithoutTax())
                .appliedTax(request.getAppliedTax())
                .taxValue(taxValue)
                .totalValue(totalValue)
                .registrationDate(request.getRegistrationDate())
                .dueDate(request.getDueDate())
                .build();

        clientCreditNoteRepository.save(creditNote);
        cacheService.refreshCache();
    }

    @Transactional
    @Override
    public void updateCreditNote(UUID invoiceId, UpdateClientCreditNoteRequest request) {
        ClientInvoiceDto invoiceDto = cacheService.getClientInvoiceById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceId));

        cacheService.getClientCreditNoteById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Credit note not found - " + request.getId()));

        Double taxValue = getTaxValue(request.getValueWithoutTax(), request.getAppliedTax());
        Double totalValue = getTotalValue(request.getValueWithoutTax(), taxValue);

        ClientCreditNoteEntity creditNoteEntity = ClientCreditNoteEntity.builder()
                .clientInvoice(new ClientInvoiceEntity(invoiceDto))
                .docNumber(request.getDocNumber())
                .description(request.getDescription())
                .valueWithoutTax(request.getValueWithoutTax())
                .appliedTax(request.getAppliedTax())
                .taxValue(taxValue)
                .totalValue(totalValue)
                .registrationDate(request.getRegistrationDate())
                .dueDate(request.getDueDate())
                .build();

        clientCreditNoteRepository.save(creditNoteEntity);
        cacheService.refreshCache();
    }

    @Transactional
    @Override
    public void deleteCreditNote(UUID invoiceId, UUID creditNoteId) {
        ClientCreditNoteEntity creditNote = clientCreditNoteRepository.findById(creditNoteId)
                .orElseThrow(() -> new EntityNotFoundException("Credit note not found - " + creditNoteId));

        if (!creditNote.getClientInvoice().getId().equals(invoiceId)) {
            throw new EntityNotFoundException("Credit note does not belong to invoice " + invoiceId);
        }

        clientCreditNoteRepository.delete(creditNote);
        cacheService.refreshCache();
    }
}

