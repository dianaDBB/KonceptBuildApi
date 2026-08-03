package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.util.FilterHelper;

import java.time.LocalDate;

public record WorkerFilter(
        String code,
        String name,
        String nif,
        Status status,
        String phone,
        String email,
        String function,
        FilterHelper.RangeFilter<Double> hourCost,
        FilterHelper.RangeFilter<Double> defaultHours,
        WorkerContractType workerContractType,
        FilterHelper.RangeFilter<Double> hourRate,
        FilterHelper.RangeFilter<Double> monthlySalary,
        FilterHelper.RangeFilter<Double> tsu,
        FilterHelper.RangeFilter<Double> mealAllowance,
        FilterHelper.RangeFilter<Double> accidentInsurance,
        FilterHelper.RangeFilter<LocalDate> startDate,
        FilterHelper.RangeFilter<LocalDate> endDate,

        WorkerSortField sortBy,
        SortDirection sortDirection) {

    public WorkerFilter {
        sortBy = sortBy == null ? WorkerSortField.CODE : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
}
