package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.ClientInvoiceDto;
import com.konceptbuild.core.dto.ClientPaymentDto;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.filter.SortDirection;
import com.konceptbuild.core.filter.ClientPaymentFilter;
import com.konceptbuild.core.filter.ClientPaymentSortField;
import com.konceptbuild.core.repository.ClientPaymentInvoiceRepository;
import com.konceptbuild.core.repository.ClientPaymentRepository;
import com.konceptbuild.core.request.CreateClientPaymentInvoiceRequest;
import com.konceptbuild.core.request.CreateClientPaymentRequest;
import com.konceptbuild.core.request.UpdateClientPaymentRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
        Comparator<ClientPaymentDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        return cacheService.getAllClientPayments().stream()
                .filter(payment -> matchesString(payment.getDocumentId(), filter.documentId()))
                .filter(payment -> filter.type() == null || filter.type() == payment.getType())
                .filter(payment -> matchesString(payment.getClient().getCompanyName(), filter.clientName()))
                .filter(payment -> isWithinRange(payment.getPaymentDate(), filter.paymentDateMin(),
                        filter.paymentDateMax()))
                .filter(payment -> isWithinRange(payment.getPaidValue(), filter.paidValueMin(), filter.paidValueMax()))
                .filter(payment -> filter.paymentMethod() == null || filter.paymentMethod() == payment.getPaymentMethod())
                .filter(payment -> matchesString(payment.getNotes(), filter.notes()))
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

    private Comparator<ClientPaymentDto> comparatorFor(ClientPaymentSortField field, SortDirection sortDirection) {
        Comparator<String> stringComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()) :
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

        Comparator<Double> doubleComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(Comparator.reverseOrder()) : Comparator.nullsLast(Comparator.naturalOrder());

        Comparator<LocalDate> dateComparator = sortDirection == SortDirection.DESC ?
                Comparator.nullsLast(Comparator.reverseOrder()) : Comparator.nullsLast(Comparator.naturalOrder());

        return switch (field) {
            case DOCUMENT_ID -> Comparator.comparing(ClientPaymentDto::getDocumentId, stringComparator);
            case PAYMENT_TYPE -> Comparator.comparing(ClientPaymentDto::getType, sortDirection == SortDirection.DESC ?
                    Comparator.nullsLast(Comparator.reverseOrder()) :
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case CLIENT_NAME -> Comparator.comparing(client -> client.getClient().getCompanyName(), stringComparator);
            case PAYMENT_DATE -> Comparator.comparing(ClientPaymentDto::getPaymentDate, dateComparator);
            case PAID_VALUE -> Comparator.comparing(ClientPaymentDto::getPaidValue, doubleComparator);
            case PAYMENT_METHOD ->
                    Comparator.comparing(ClientPaymentDto::getPaymentMethod, sortDirection == SortDirection.DESC ?
                            Comparator.nullsLast(Comparator.reverseOrder()) :
                            Comparator.nullsLast(Comparator.naturalOrder()));
            case NOTES -> Comparator.comparing(ClientPaymentDto::getNotes, stringComparator);
        };
    }

    @Transactional
    @Override
    public void add(CreateClientPaymentRequest request) {
        ClientDto client = cacheService.getClientById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.getClientId()));

        ClientPaymentEntity payment = ClientPaymentEntity
                .builder()
                .client(new ClientEntity(client))
                .type(request.getType())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .paidValue(request.getPaidValue())
                .build();

        payment = clientPaymentRepository.save(payment);

        for (CreateClientPaymentInvoiceRequest invoiceDto : request.getInvoices()) {
            ClientInvoiceDto invoice = cacheService.getClientInvoiceById(invoiceDto.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceDto.getInvoiceId()));

            ClientPaymentInvoiceEntity paymentInvoiceEntity = ClientPaymentInvoiceEntity
                    .builder()
                    .id(new ClientPaymentInvoiceId(payment.getId(), invoice.getId()))
                    .payment(payment).invoice(new ClientInvoiceEntity(invoice))
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

        ClientPaymentEntity paymentEntity = new ClientPaymentEntity(paymentDto);
        paymentEntity.setType(request.getType());
        paymentEntity.setClient(new ClientEntity(clientDto));
        paymentEntity.setPaymentDate(request.getPaymentDate());
        paymentEntity.setPaidValue(request.getPaidValue());
        paymentEntity.setPaymentMethod(request.getPaymentMethod());
        paymentEntity.setNotes(request.getNotes());
        clientPaymentRepository.save(paymentEntity);

        // Delete existing associations
        clientPaymentInvoiceRepository.deleteByPayment_Id(request.getId());

        // Recreate associations
        for (CreateClientPaymentInvoiceRequest invoiceRequest : request.getInvoices()) {
            ClientInvoiceDto invoice = cacheService.getClientInvoiceById(invoiceRequest.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found - " + invoiceRequest.getInvoiceId()));

            ClientPaymentInvoiceEntity paymentInvoiceEntity = ClientPaymentInvoiceEntity
                    .builder()
                    .id(new ClientPaymentInvoiceId(request.getId(), invoice.getId()))
                    .payment(paymentEntity)
                    .invoice(new ClientInvoiceEntity(invoice))
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
