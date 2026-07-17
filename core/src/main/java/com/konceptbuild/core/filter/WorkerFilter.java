package com.konceptbuild.core.filter;

import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.enums.Status;

import java.time.LocalDate;

public record WorkerFilter(
        String code,
        String name,
        String nif,
        Status status,
        String phone,
        String email,
        String function,
        Double hourCostMin,
        Double hourCostMax,
        Double defaultHoursMin,
        Double defaultHoursMax,
        WorkerContractType workerContractType,
        Double hourRateMin,
        Double hourRateMax,
        Double monthlySalaryMin,
        Double monthlySalaryMax,
        Double tsuMin,
        Double tsuMax,
        Double mealAllowanceMin,
        Double mealAllowanceMax,
        Double accidentInsuranceMin,
        Double accidentInsuranceMax,
        LocalDate startDateMin,
        LocalDate startDateMax,
        LocalDate endDateMin,
        LocalDate endDateMax,

        WorkerSortField sortBy,
        SortDirection sortDirection) {

    public WorkerFilter {
        sortBy = sortBy == null ? WorkerSortField.NAME : sortBy;
        sortDirection = sortDirection == null ? SortDirection.ASC : sortDirection;

        validateRange("hourCost", hourCostMin, hourCostMax);
        validateRange("defaultHours", defaultHoursMin, defaultHoursMax);
        validateRange("hourRate", hourRateMin, hourRateMax);
        validateRange("monthlySalary", monthlySalaryMin, monthlySalaryMax);
        validateRange("tsu", tsuMin, tsuMax);
        validateRange("mealAllowance", mealAllowanceMin, mealAllowanceMax);
        validateRange("accidentInsurance", accidentInsuranceMin, accidentInsuranceMax);
        validateRange("startDate", startDateMin, startDateMax);
        validateRange("endDate", endDateMin, endDateMin);
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
