package com.konceptbuild.core.filter;

public enum WageSortField {
    CODE("code"),
    YEAR("year"),
    MONTH("month"),
    WORKER_CODE("workerCode"),
    WORKER_NAME("workerName"),
    EXPECTED_WAGE("expectedWage"),
    EXPECTED_EXTRA_HOURS("expectedExtraHours"),
    EXPECTED_DEDUCTIONS("expectedDeductions"),
    EXPECTED_INTERNAL_COST("expectedInternalCost"),
    PAID_VALUE("paidValue"),
    PAID_DATE("paidDate"),
    PAYMENT_METHOD("paymentMethod"),
    NOTES("notes");

    private final String fieldName;

    WageSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
