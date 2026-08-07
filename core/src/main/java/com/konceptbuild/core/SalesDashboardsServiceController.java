package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.enums.InvoiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesDashboardsServiceController implements SalesDashboardsService {
    @Autowired
    private CacheService cacheService;

    @Override
    public SalesDashboardDto getDashboard() {
        List<ClientInvoiceDto> invoices = cacheService.getAllClientInvoices();
        List<ClientPaymentDto> payments = cacheService.getAllClientPayments();

        Double totalBilled = invoices.stream()
                .mapToDouble(ClientInvoiceDto::getTotalValueGross)
                .sum();

        Double totalReceivedValue = payments.stream()
                .mapToDouble(payment -> switch (payment.getType()) {
                    case PAYMENT -> Optional.ofNullable(payment.getTotalPaidValue()).orElse(0.0);
                    case REFUND -> -Optional.ofNullable(payment.getTotalPaidValue()).orElse(0.0);
                })
                .sum();

        Double totalDue = invoices.stream()
                .mapToDouble(ClientInvoiceDto::getAmountDueWithTax)
                .sum();

        List<SalesDashboardRowDto> clientsStatistics = invoices.stream()
                .collect(Collectors.groupingBy(ClientInvoiceDto::getClient))
                .entrySet()
                .stream()
                .map(entry -> {
                    ClientDto client = entry.getKey();
                    List<ClientInvoiceDto> clientInvoices = entry.getValue();

                    SalesDashboardRowDto row = new SalesDashboardRowDto();
                    row.setClient(client);

                    row.setTotalBilled(clientInvoices.stream()
                            .mapToDouble(i -> Optional.ofNullable(i.getTotalValueGross()).orElse(0.0))
                            .sum());

                    row.setTotalReceived(clientInvoices.stream()
                            .mapToDouble(i -> Optional.ofNullable(i.getAmountReceivedWithTax()).orElse(0.0))
                            .sum());

                    row.setTotalDue(clientInvoices.stream()
                            .mapToDouble(i -> Optional.ofNullable(i.getAmountDueWithTax()).orElse(0.0))
                            .sum());

                    row.setTotalOverdue(clientInvoices.stream()
                            .filter(i -> i.getStatus() == InvoiceStatus.DELAY)
                            .mapToDouble(i -> Optional.ofNullable(i.getAmountDueWithTax()).orElse(0.0))
                            .sum());

                    InvoiceStatus status = clientInvoices.stream()
                            .map(ClientInvoiceDto::getStatus)
                            .filter(Objects::nonNull)
                            .max(Comparator.comparingInt(STATUS_PRIORITY::get))
                            .orElse(InvoiceStatus.PAID);

                    row.setStatus(status);

                    return row;
                })
                .sorted(Comparator.comparing(row -> row.getClient().getCode()))
                .toList();

        return SalesDashboardDto.builder()
                .totalBilled(totalBilled)
                .totalReceived(totalReceivedValue)
                .totalDue(totalDue)
                .clientsStatistics(clientsStatistics)
                .build();
    }

    @Override
    public SalesClientReportDto getClientReport(UUID clientId) {
        List<ClientInvoiceDto> invoices = cacheService.getAllClientInvoicesByClientId(clientId)
                .stream()
                .sorted(Comparator.comparing(ClientInvoiceDto::getDocNumber))
                .toList();

        Double totalValueWithTax = invoices.stream()
                .mapToDouble(ClientInvoiceDto::getTotalValue)
                .sum();

        Double totalValueGross = invoices.stream()
                .mapToDouble(ClientInvoiceDto::getTotalValueGross)
                .sum();

        Double totalAmountDueWithTax = invoices.stream()
                .mapToDouble(ClientInvoiceDto::getAmountDueWithTax)
                .sum();

        return SalesClientReportDto.builder()
                .invoices(invoices)
                .totalValueWithTax(totalValueWithTax)
                .totalValueGross(totalValueGross)
                .totalAmountDueWithTax(totalAmountDueWithTax)
                .build();
    }

    private static final Map<InvoiceStatus, Integer> STATUS_PRIORITY = Map.of(
            InvoiceStatus.PAID, 0,
            InvoiceStatus.PENDING, 1,
            InvoiceStatus.PARTIAL, 2,
            InvoiceStatus.DELAY, 3
    );
}
