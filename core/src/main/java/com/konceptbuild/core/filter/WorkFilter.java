package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.util.FilterHelper;

import java.time.LocalDate;

public record WorkFilter(
        String code,
        String name,
        WorkStatus status,
        FilterHelper.RangeFilter<Double> contractedBudget,
        FilterHelper.RangeFilter<Double> estimatedCost,
        FilterHelper.RangeFilter<Double> estimatedCostMaterials,
        FilterHelper.RangeFilter<Double> estimatedCostLabor,
        FilterHelper.RangeFilter<Double> estimatedMarginEur,
        FilterHelper.RangeFilter<Double> estimatedMarginPercentual,
        FilterHelper.RangeFilter<LocalDate> startDate,
        FilterHelper.RangeFilter<LocalDate> estimatedEndDate,
        FilterHelper.RangeFilter<LocalDate> endDate,
        String client,

        WorkSortField sortBy,
        SortDirection sortDirection) {

    public WorkFilter {
        sortBy = sortBy == null ? WorkSortField.CODE : sortBy;
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }
}
