package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.ClientInvoiceDto;
import com.konceptbuild.core.dto.ClientPaymentDto;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.filter.ClientPaymentFilter;
import com.konceptbuild.core.repository.ClientPaymentInvoiceRepository;
import com.konceptbuild.core.repository.ClientPaymentRepository;
import com.konceptbuild.core.request.CreateClientPaymentInvoiceRequest;
import com.konceptbuild.core.request.CreateClientPaymentRequest;
import com.konceptbuild.core.request.UpdateClientPaymentRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ClientPaymentServiceImpl implements ClientPaymentService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private ClientPaymentRepository clientPaymentRepository;

    @Autowired
    private ClientPaymentInvoiceRepository clientPaymentInvoiceRepository;

    @Override
    public List<ClientPaymentDto> search(ClientPaymentFilter filter) {
        Comparator<ClientPaymentDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                ClientPaymentDto.class
        );

        return cacheService.getAllClientPayments().stream()
                .filter(payment -> FilterHelper.matchesString(payment.getDocumentId(), filter.documentId()))
                .filter(payment -> FilterHelper.matchesEnum(payment.getType(), filter.type()))
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
                .filter(payment -> FilterHelper.isWithinRange(payment.getPaymentDate(), filter.paymentDate()))
                .filter(payment -> FilterHelper.isWithinRange(payment.getTotalPaidValue(), filter.totalPaidValue()))
                .filter(payment -> FilterHelper.matchesEnum(payment.getPaymentMethod(), filter.paymentMethod()))
                .filter(payment -> FilterHelper.matchesString(payment.getNotes(), filter.notes()))
                .sorted(comparator)
                .toList();
    }

    @Transactional
    @Override
    public void add(CreateClientPaymentRequest request) {
        ClientDto clientDto = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        Double totalPaidValue = request.getPaidInvoices().stream()
                .mapToDouble(CreateClientPaymentInvoiceRequest::getPaidValue)
                .sum();

        ClientPaymentEntity payment = ClientPaymentEntity
                .builder()
                .client(new ClientEntity(clientDto))
                .type(request.getType())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .totalPaidValue(totalPaidValue)
                .build();

        payment = clientPaymentRepository.save(payment);

        for (CreateClientPaymentInvoiceRequest invoiceRequest : request.getPaidInvoices()) {
            ClientInvoiceDto invoice = cacheService.getClientInvoiceById(invoiceRequest.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceRequest.getInvoiceId()));

            if (invoice.getClient().getId() != clientDto.getId()) {
                throw new EntityNotFoundException("Invoice " + invoice.getDocNumber() + " does not belong to the " +
                        "selected client "
                        + clientDto.getCode());
            }

            ClientPaymentInvoiceEntity paymentInvoiceEntity = ClientPaymentInvoiceEntity
                    .builder()
                    .id(new ClientPaymentInvoiceId(payment.getId(), invoice.getId()))
                    .payment(payment).invoice(new ClientInvoiceEntity(invoice))
                    .paidValue(invoiceRequest.getPaidValue())
                    .build();

            clientPaymentInvoiceRepository.save(paymentInvoiceEntity);
        }

        cacheService.refreshCache();
    }

    @Override
    public void update(UpdateClientPaymentRequest request) {
        ClientPaymentDto paymentDto = cacheService.getClientPaymentById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found - " + request.getId()));

        ClientDto clientDto = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        Double totalPaidValue = request.getPaidInvoices().stream()
                .mapToDouble(CreateClientPaymentInvoiceRequest::getPaidValue)
                .sum();

        ClientPaymentEntity paymentEntity = new ClientPaymentEntity(paymentDto);
        paymentEntity.setType(request.getType());
        paymentEntity.setClient(new ClientEntity(clientDto));
        paymentEntity.setPaymentDate(request.getPaymentDate());
        paymentEntity.setTotalPaidValue(totalPaidValue);
        paymentEntity.setPaymentMethod(request.getPaymentMethod());
        paymentEntity.setNotes(request.getNotes());
        clientPaymentRepository.save(paymentEntity);

        // Delete existing associations
        clientPaymentInvoiceRepository.deleteByPayment_Id(request.getId());

        // Recreate associations
        for (CreateClientPaymentInvoiceRequest invoiceRequest : request.getPaidInvoices()) {
            ClientInvoiceDto invoice = cacheService.getClientInvoiceById(invoiceRequest.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceRequest.getInvoiceId()));

            if (invoice.getClient().getId() != clientDto.getId()) {
                throw new EntityNotFoundException("Invoice " + invoice.getDocNumber() + " does not belong to the " +
                        "selected client "
                        + clientDto.getCode());
            }

            ClientPaymentInvoiceEntity paymentInvoiceEntity = ClientPaymentInvoiceEntity
                    .builder()
                    .id(new ClientPaymentInvoiceId(request.getId(), invoice.getId()))
                    .payment(paymentEntity)
                    .invoice(new ClientInvoiceEntity(invoice))
                    .paidValue(invoiceRequest.getPaidValue())
                    .build();

            clientPaymentInvoiceRepository.save(paymentInvoiceEntity);
        }

        cacheService.refreshCache();
    }

    @Override
    public void delete(UUID id) {
        ClientPaymentEntity payment = clientPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found - " + id));

        clientPaymentInvoiceRepository.deleteByPayment_Id(id);
        clientPaymentRepository.delete(payment);

        cacheService.refreshCache();
    }
}
