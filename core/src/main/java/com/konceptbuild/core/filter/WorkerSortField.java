package com.konceptbuild.core.filter;

public enum WorkerSortField {
    CODE("code"),
    NAME("name"),
    NIF("nif"),
    STATUS("status"),
    PHONE("phone"),
    EMAIL("email"),
    FUNCTION("function"),
    HOUR_COST("currentWorkerCompensation.hourCost"),
    DEFAULT_HOURS("currentWorkerCompensation.defaultHours"),
    CONTRACT_TYPE("contractType"),
    HOUR_RATE("currentWorkerCompensation.hourRate"),
    MONTHLY_SALARY("currentWorkerCompensation.monthlySalary"),
    TSU("currentWorkerCompensation.tsu"),
    MEAL_ALLOWANCE("currentWorkerCompensation.mealAllowance"),
    ACCIDENT_INSURANCE("currentWorkerCompensation.accidentInsurance"),
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
