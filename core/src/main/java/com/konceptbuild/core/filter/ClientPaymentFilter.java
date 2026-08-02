package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.enums.ClientPaymentType;

import java.time.LocalDate;

public record ClientPaymentFilter(
        String documentId,
        ClientPaymentType type,
        String clientName,
        LocalDate paymentDateMin,
        LocalDate paymentDateMax,
        Double paidValueMin,
        Double paidValueMax,
        PaymentMethod paymentMethod,
        String notes,

        ClientPaymentSortField sortBy,
        SortDirection sortDirection) {

    public ClientPaymentFilter {
        sortBy = sortBy == null ? ClientPaymentSortField.DOCUMENT_ID : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;

        validateRange("paymentDate", paymentDateMin, paymentDateMax);
        validateRange("paidValue", paidValueMin, paidValueMax);
    }

    private static void validateRange(String field, Double min, Double max) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException(field + " - MIN must not exceed MAX");
        }
    }

    private static void validateRange(String field, LocalDate min, LocalDate max) {
        if (min != null && max != null && min.isAfter(max)) {
            throw new IllegalArgumentException(field + " - MIN must not exceed MAX");
        }
    }
}
