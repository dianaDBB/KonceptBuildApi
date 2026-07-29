package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.PaymentMethod;

import java.time.LocalDate;

public record WageFilter(
        Integer yearMax,
        Integer yearMin,
        Integer monthMax,
        Integer monthMin,
        String workerCode,
        String workerName,
        Double expectedWageMin,
        Double expectedWageMax,
        Double expectedExtraHoursMin,
        Double expectedExtraHoursMax,
        Double expectedDeductionsMin,
        Double expectedDeductionsMax,
        Double expectedInternalCostMin,
        Double expectedInternalCostMax,
        Double expectedPayMin,
        Double expectedPayMax,
        Double paidValueMin,
        Double paidValueMax,
        LocalDate paidDateMin,
        LocalDate paidDateMax,
        PaymentMethod paymentMethod,
        String notes,

        WageSortField sortBy,
        SortDirection sortDirection) {

    public WageFilter {
        sortBy = sortBy == null ? WageSortField.CODE : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;

        validateRange("expectedWage", expectedWageMin, expectedWageMax);
        validateRange("expectedExtraHours", expectedExtraHoursMin, expectedExtraHoursMax);
        validateRange("expectedDeductions", expectedDeductionsMin, expectedDeductionsMax);
        validateRange("expectedInternalCost", expectedInternalCostMin, expectedInternalCostMax);
        validateRange("expectedPay", expectedPayMin, expectedPayMax);
        validateRange("paidValue", paidValueMin, paidValueMax);
        validateRange("paidDate", paidDateMin, paidDateMax);
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
