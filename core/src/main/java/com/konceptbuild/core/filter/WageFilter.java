package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.util.FilterHelper;

import java.time.LocalDate;

public record WageFilter(
        String code,
        FilterHelper.RangeFilter<Integer> year,
        FilterHelper.RangeFilter<Integer> month,
        String workerCode,
        String workerName,
        FilterHelper.RangeFilter<Double> expectedWage,
        FilterHelper.RangeFilter<Double> expectedExtraHours,
        FilterHelper.RangeFilter<Double> expectedDeductions,
        FilterHelper.RangeFilter<Double> expectedInternalCost,
        FilterHelper.RangeFilter<Double> expectedPay,
        FilterHelper.RangeFilter<Double> paidValue,
        FilterHelper.RangeFilter<LocalDate> paidDate,
        PaymentMethod paymentMethod,
        String notes,

        WageSortField sortBy,
        SortDirection sortDirection) {

    public WageFilter {
        sortBy = sortBy == null ? WageSortField.CODE : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
}
