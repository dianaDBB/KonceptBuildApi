package com.konceptbuild.core.dto;

import java.util.UUID;

public record WorkerFilter(
        UUID id,
        String name,
        WorkerType workerType,
        Double minHourRate,
        Double maxHourRate,
        Double minMonthlySalary,
        Double maxMonthlySalary,
        Double minHourCost,
        Double maxHourCost,
        WorkerSortField sortBy,
        SortDirection sortDirection) {

    public WorkerFilter {
        sortBy = sortBy == null ? WorkerSortField.NAME : sortBy;
        sortDirection = sortDirection == null ? SortDirection.ASC : sortDirection;
        validateRange("hourRate", minHourRate, maxHourRate);
        validateRange("monthlySalary", minMonthlySalary, maxMonthlySalary);
        validateRange("hourCost", minHourCost, maxHourCost);
    }

    private static void validateRange(String field, Double min, Double max) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("min" + capitalize(field) + " must not exceed max" + capitalize(field));
        }
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
