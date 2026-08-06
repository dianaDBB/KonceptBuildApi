package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.enums.Aging;
import com.konceptbuild.core.enums.ClientPaymentType;
import com.konceptbuild.core.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInvoiceDto {
    private UUID id;
    private String docNumber;
    private ClientDto client;
    private WorkDto work;
    private String description;
    private Double valueWithoutTax;
    private Double appliedTax;
    private Double taxValue;
    private Double totalValue;
    private LocalDate registrationDate;
    private LocalDate dueDate;
    private List<ClientCreditNoteDto> creditNotes;

    private Double sumCreditNotesWithoutTax;
    private Double sumCreditNotesWithTax;

    private Double totalValueNet;
    private Double totalValueGross;

    private Double amountReceivedWithoutTax;
    private Double amountReceivedWithTax;

    private Double amountDueWithoutTax;
    private Double amountDueWithTax;

    private Integer paymentsCount;
    private InvoiceStatus status;
    private Integer daysPastDue;
    private Aging aging;
    private LocalDate settlementDate;
    private Integer daysToPay;

    public ClientInvoiceDto(ClientInvoiceEntity entity) {
        this.id = entity.getId();
        this.docNumber = entity.getDocNumber();
        this.client = new ClientDto(entity.getClient());
        this.work = new WorkDto(entity.getWork(), this.client);
        this.description = entity.getDescription();
        this.valueWithoutTax = entity.getValueWithoutTax();
        this.appliedTax = entity.getAppliedTax();
        this.taxValue = entity.getTaxValue();
        this.totalValue = entity.getTotalValue();
        this.registrationDate = entity.getRegistrationDate();
        this.dueDate = entity.getDueDate();
        this.creditNotes = entity.getCreditNotes() == null
                ? List.of()
                : entity.getCreditNotes().stream()
                .map(ClientCreditNoteDto::new)
                .toList();
    }

    public void calculateStatistics(List<ClientPaymentDto> payments) {
        this.sumCreditNotesWithoutTax = creditNotes.stream()
                .mapToDouble(ClientCreditNoteDto::getValueWithoutTax)
                .sum();
        this.sumCreditNotesWithTax = creditNotes.stream()
                .mapToDouble(ClientCreditNoteDto::getTotalValue)
                .sum();

        this.totalValueNet = valueWithoutTax - sumCreditNotesWithoutTax;
        this.totalValueGross = totalValue - sumCreditNotesWithTax;

        Double totalPaidValue = payments.stream()
                .filter(payment -> payment.getType() == ClientPaymentType.PAYMENT)
                .mapToDouble(ClientPaymentDto::getTotalPaidValue)
                .sum();
        Double totalRefundValue = payments.stream()
                .filter(payment -> payment.getType() == ClientPaymentType.REFUND)
                .mapToDouble(ClientPaymentDto::getTotalPaidValue)
                .sum();
        this.amountReceivedWithTax = totalPaidValue - totalRefundValue;
        this.amountReceivedWithoutTax = totalValueGross == 0 ? 0 :
                amountReceivedWithTax * (totalValueNet / totalValueGross);

        this.amountDueWithoutTax = valueWithoutTax - amountReceivedWithoutTax - sumCreditNotesWithoutTax;
        this.amountDueWithTax = totalValue - amountReceivedWithTax - sumCreditNotesWithTax;

        this.paymentsCount = payments.size();

        this.status = amountDueWithTax == 0 ? InvoiceStatus.PAID :
                amountReceivedWithTax > 0 ? InvoiceStatus.PARTIAL :
                        LocalDate.now().isAfter(dueDate) ? InvoiceStatus.DELAY : InvoiceStatus.PENDING;

        // dueDate is before today > 0 | dueDate is today = 0 | dueDate is after today < 0
        this.daysPastDue = Math.toIntExact(ChronoUnit.DAYS.between(dueDate, LocalDate.now()));

        this.aging = status == InvoiceStatus.PAID ? Aging.PAID :
                daysPastDue <= 0 ? Aging.NOT_YET_DUE :
                        daysPastDue <= 30 ? Aging.ZERO_THIRTY :
                                daysPastDue <= 60 ? Aging.THIRTY_SIXTY :
                                        daysPastDue <= 90 ? Aging.SIXTY_NINTY : Aging.NINTY_PLUS;

        this.settlementDate = amountDueWithTax > 0 ? null :
                payments.stream()
                        .filter(payment -> payment.getType() == ClientPaymentType.PAYMENT)
                        .map(ClientPaymentDto::getPaymentDate)
                        .max(LocalDate::compareTo)
                        .orElse(null);

        this.daysToPay = settlementDate == null ? null : Math.toIntExact(ChronoUnit.DAYS.between(registrationDate,
                settlementDate));
    }
}
