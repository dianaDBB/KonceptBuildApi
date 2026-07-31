package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.filter.ClientInvoiceFilter;
import com.konceptbuild.core.filter.ClientInvoiceSortField;
import com.konceptbuild.core.filter.SortDirection;
import com.konceptbuild.core.repository.ClientInvoiceRepository;
import com.konceptbuild.core.request.CreateClientInvoiceRequest;
import com.konceptbuild.core.request.UpdateClientInvoiceRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ClientInvoiceServiceImpl implements ClientInvoiceService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ClientInvoiceRepository clientInvoiceRepository;

    @Override
    public List<ClientInvoiceDto> search(ClientInvoiceFilter filter) {
        Comparator<ClientInvoiceDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        return cacheService.getAllClientInvoices().stream()
                .filter(invoice -> matchesString(invoice.getDocNumber(), filter.docNumber()))
                .filter(invoice -> matchesString(invoice.getClient().getCompanyName(), filter.clientName()))
                .filter(invoice -> matchesString(invoice.getWork().getName(), filter.workName()))
                .filter(invoice -> matchesString(invoice.getDescription(), filter.description()))
                .filter(invoice -> isWithinRange(invoice.getValueWithoutTax(), filter.valueWithoutTaxMin(),
                        filter.valueWithoutTaxMax()))
                .filter(invoice -> isWithinRange(invoice.getAppliedTax(), filter.appliedTaxMin(),
                        filter.appliedTaxMax()))
                .filter(invoice -> isWithinRange(invoice.getTaxValue(), filter.taxValueMin(), filter.taxValueMax()))
                .filter(invoice -> isWithinRange(invoice.getTotalValue(), filter.totalValueMin(),
                        filter.totalValueMax()))
                .filter(invoice -> isWithinRange(invoice.getRegistrationDate(), filter.registrationDateMin(),
                        filter.registrationDateMax()))
                .filter(invoice -> isWithinRange(invoice.getDueDate(), filter.dueDateMin(), filter.dueDateMax()))
                .sorted(comparator)
                .toList();
    }

    private boolean matchesString(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    private boolean isWithinRange(Double value, Double min, Double max) {
        return (min == null || value != null && value >= min) && (max == null || value != null && value <= max);
    }

    private boolean isWithinRange(LocalDate value, LocalDate min, LocalDate max) {
        return value == null || (min == null || !value.isBefore(min)) && (max == null || !value.isAfter(max));
    }

    private Comparator<ClientInvoiceDto> comparatorFor(ClientInvoiceSortField field, SortDirection sortDirection) {
        Comparator<String> stringComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()) :
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

        Comparator<Double> doubleComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(Comparator.reverseOrder()) : Comparator.nullsLast(Comparator.naturalOrder());

        Comparator<LocalDate> dateComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(Comparator.reverseOrder()) : Comparator.nullsLast(Comparator.naturalOrder());

        return switch (field) {
            case DOCUMENT_NUMBER -> Comparator.comparing(ClientInvoiceDto::getDocNumber, stringComparator);
            case CLIENT_NAME ->
                    Comparator.comparing(clientInvoice -> clientInvoice.getClient().getCompanyName(), stringComparator);
            case WORK_NAME ->
                    Comparator.comparing(clientInvoice -> clientInvoice.getWork().getName(), stringComparator);
            case DESCRIPTION -> Comparator.comparing(ClientInvoiceDto::getDescription, stringComparator);
            case VALUE_WITHOUT_TAX -> Comparator.comparing(ClientInvoiceDto::getValueWithoutTax, doubleComparator);
            case APPLIED_TAX -> Comparator.comparing(ClientInvoiceDto::getAppliedTax, doubleComparator);
            case TAX_VALUE -> Comparator.comparing(ClientInvoiceDto::getTaxValue, doubleComparator);
            case TOTAL_VALUE -> Comparator.comparing(ClientInvoiceDto::getTotalValue, doubleComparator);
            case REGISTRATION_DATE -> Comparator.comparing(ClientInvoiceDto::getRegistrationDate, dateComparator);
            case DUE_DATE -> Comparator.comparing(ClientInvoiceDto::getDueDate, dateComparator);
        };
    }

    @Transactional
    @Override
    public void add(CreateClientInvoiceRequest request) {
        ClientDto client = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        WorkDto workDto = cacheService.getWorkById(request.getWorkId())
                .orElseThrow(() -> new EntityNotFoundException("Work not found - " + request.getWorkId()));

        Double taxValue = request.getValueWithoutTax() * (request.getAppliedTax() / 100);
        Double totalValue = request.getValueWithoutTax() + taxValue;
        ClientInvoiceEntity invoiceEntity = ClientInvoiceEntity
                .builder()
                .docNumber(request.getDocNumber())
                .client(new ClientEntity(client))
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
        ClientInvoiceDto invoiceDto = cacheService.getClientInvoiceById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + request.getId()));

        ClientDto clientDto = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        WorkDto workDto = cacheService.getWorkById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Work not found - " + request.getClientId()));

        ClientInvoiceEntity invoiceEntity = new ClientInvoiceEntity(invoiceDto);
        Double taxValue = request.getValueWithoutTax() * (request.getAppliedTax() / 100);
        Double totalValue = request.getValueWithoutTax() + taxValue;
        invoiceEntity.setDocNumber(request.getDocNumber());
        invoiceEntity.setClient(new ClientEntity(clientDto));
        invoiceEntity.setWork(new WorkEntity(workDto));
        invoiceEntity.setDescription(request.getDescription());
        invoiceEntity.setValueWithoutTax(request.getValueWithoutTax());
        invoiceEntity.setAppliedTax(request.getAppliedTax());
        invoiceEntity.setTaxValue(taxValue);
        invoiceEntity.setTotalValue(totalValue);
        invoiceEntity.setRegistrationDate(request.getRegistrationDate());
        invoiceEntity.setDueDate(request.getDueDate());

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
}
