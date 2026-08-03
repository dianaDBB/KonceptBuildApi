package com.konceptbuild.core.filter;

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

        ClientInvoiceSortField sortBy,
        SortDirection sortDirection) {

    public ClientInvoiceFilter {
        sortBy = sortBy == null ? ClientInvoiceSortField.DOCUMENT_NUMBER : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
}
