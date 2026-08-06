package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.Aging;
import com.konceptbuild.core.enums.InvoiceStatus;
import com.konceptbuild.core.util.FilterHelper;

import java.time.LocalDate;
import java.util.UUID;

public record ClientInvoiceFilter(
        UUID id,
        String docNumber,
        String client,
        String work,
        String description,
        FilterHelper.RangeFilter<Double> valueWithoutTax,
        FilterHelper.RangeFilter<Double> appliedTax,
        FilterHelper.RangeFilter<Double> taxValue,
        FilterHelper.RangeFilter<Double> totalValue,
        FilterHelper.RangeFilter<LocalDate> registrationDate,
        FilterHelper.RangeFilter<LocalDate> dueDate,
        FilterHelper.RangeFilter<Double> sumCreditNotesWithoutTax,
        FilterHelper.RangeFilter<Double> sumCreditNotesWithTax,
        FilterHelper.RangeFilter<Double> totalValueNet,
        FilterHelper.RangeFilter<Double> totalValueGross,
        FilterHelper.RangeFilter<Double> amountReceivedWithoutTax,
        FilterHelper.RangeFilter<Double> amountReceivedWithTax,
        FilterHelper.RangeFilter<Double> amountDueWithoutTax,
        FilterHelper.RangeFilter<Double> amountDueWithTax,
        FilterHelper.RangeFilter<Integer> paymentsCount,
        InvoiceStatus status,
        FilterHelper.RangeFilter<Integer> daysPastDue,
        Aging aging,
        FilterHelper.RangeFilter<LocalDate> settlementDate,
        FilterHelper.RangeFilter<Integer> daysToPay,

        ClientInvoiceSortField sortBy,
        SortDirection sortDirection) {

    public ClientInvoiceFilter {
        sortBy = sortBy == null ? ClientInvoiceSortField.DOCUMENT_NUMBER : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
}
