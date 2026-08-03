package com.konceptbuild.core.filter;

public enum WorkerSortField {
    CODE("code"),
    NAME("name"),
    NIF("nif"),
    STATUS("status"),
    PHONE("phone"),
    EMAIL("email"),
    FUNCTION("function"),
    HOUR_COST("hourCost"),
    DEFAULT_HOURS("defaultHours"),
    CONTRACT_TYPE("contractType"),
    HOUR_RATE("hourRate"),
    MONTHLY_SALARY("monthlySalary"),
    TSU("tsu"),
    MEAL_ALLOWANCE("mealAllowance"),
    ACCIDENT_INSURANCE("accidentInsurance"),
    START_DATE("startDate"),
    END_DATE("endDate");

    private final String fieldName;

    WorkerSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
