package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.filter.ClientInvoiceFilter;
import com.konceptbuild.core.repository.ClientInvoiceRepository;
import com.konceptbuild.core.request.CreateClientInvoiceRequest;
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

