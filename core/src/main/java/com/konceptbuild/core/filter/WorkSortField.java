package com.konceptbuild.core.filter;

public enum WorkSortField {
    CODE("code"),
    NAME("name"),
    STATUS("status"),
    CONTRACTED_BUDGET("contractedBudget"),
    ESTIMATED_COST("estimatedCost"),
    ESTIMATED_COST_MATERIALS("estimatedCostMaterials"),
    ESTIMATED_COST_LABOR("estimatedCostLabor"),
    ESTIMATED_MARGIN_EUR("estimatedMarginEur"),
    ESTIMATED_MARGIN_PERCENTUAL("estimatedMarginPercentual"),
    START_DATE("startDate"),
    ESTIMATED_END_DATE("estimatedEndDate"),
    END_DATE("endDate"),
    CLIENT("client.code");

    private final String fieldName;

    WorkSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
