package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.enums.ClientPaymentType;
import com.konceptbuild.core.util.FilterHelper;

import java.time.LocalDate;

public record ClientPaymentFilter(
        String documentId,
        ClientPaymentType type,
        String client,
        FilterHelper.RangeFilter<LocalDate> paymentDate,
        FilterHelper.RangeFilter<Double> paidValue,
        PaymentMethod paymentMethod,
        String notes,

        ClientPaymentSortField sortBy,
        SortDirection sortDirection) {

    public ClientPaymentFilter {
        sortBy = sortBy == null ? ClientPaymentSortField.DOCUMENT_ID : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;

    }
}
