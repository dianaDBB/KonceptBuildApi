package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.WorkStatus;

import java.time.LocalDate;

public record WorkFilter(
        String code,
        String name,
        WorkStatus status,
        Double contractedBudgetMin,
        Double contractedBudgetMax,
        Double estimatedCostMin,
        Double estimatedCostMax,
        Double estimatedCostMaterialsMin,
        Double estimatedCostMaterialsMax,
        Double estimatedCostLaborMin,
        Double estimatedCostLaborMax,
        Double estimatedMarginEurMin,
        Double estimatedMarginEurMax,
        Double estimatedMarginPercentualMin,
        Double estimatedMarginPercentualMax,
        LocalDate startDateMin,
        LocalDate startDateMax,
        LocalDate estimatedEndDateMin,
        LocalDate estimatedEndDateMax,
        LocalDate endDateMin,
        LocalDate endDateMax,
        String clientName,

        WorkSortField sortBy,
        SortDirection sortDirection) {

    public WorkFilter {
        sortBy = sortBy == null ? WorkSortField.NAME : sortBy;
        sortDirection = sortDirection == null ? SortDirection.ASC : sortDirection;

        validateRange("contractedBudget", contractedBudgetMin, contractedBudgetMax);
        validateRange("estimatedCost", estimatedCostMin, estimatedCostMax);
        validateRange("estimatedCostMaterials", estimatedCostMaterialsMin, estimatedCostMaterialsMax);
        validateRange("estimatedCostLabor", estimatedCostLaborMin, estimatedCostLaborMax);
        validateRange("estimatedMarginEur", estimatedMarginEurMin, estimatedMarginEurMax);
        validateRange("estimatedMarginPercentual", estimatedMarginPercentualMin, estimatedMarginPercentualMax);
        validateRange("startDate", startDateMin, startDateMax);
        validateRange("estimatedEndDate", estimatedEndDateMin, estimatedEndDateMax);
        validateRange("endDate", endDateMin, endDateMax);
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
