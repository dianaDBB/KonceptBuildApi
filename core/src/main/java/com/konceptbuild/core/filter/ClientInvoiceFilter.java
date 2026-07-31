package com.konceptbuild.core.filter;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.WorkDto;

import java.time.LocalDate;
import java.util.UUID;

public record ClientInvoiceFilter(
        UUID id,
        String docNumber,
        ClientDto client,
        WorkDto work,
        String description,
        Double valueWithoutTaxMin,
        Double valueWithoutTaxMax,
        Double appliedTaxMin,
        Double appliedTaxMax,
        Double taxValueMin,
        Double taxValueMax,
        Double totalValueMin,
        Double totalValueMax,
        LocalDate registrationDateMin,
        LocalDate registrationDateMax,
        LocalDate dueDateMin,
        LocalDate dueDateMax,

        ClientInvoiceSortField sortBy,
        SortDirection sortDirection) {

    public ClientInvoiceFilter {
        sortBy = sortBy == null ? ClientInvoiceSortField.DOCUMENT_NUMBER : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;

        validateRange("valueWithoutTax", valueWithoutTaxMin, valueWithoutTaxMax);
        validateRange("appliedTax", appliedTaxMin, appliedTaxMax);
        validateRange("taxValue", taxValueMin, taxValueMax);
        validateRange("totalValue", totalValueMin, totalValueMax);
        validateRange("registrationDate", registrationDateMin, registrationDateMax);
        validateRange("dueDate", dueDateMin, dueDateMax);
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
