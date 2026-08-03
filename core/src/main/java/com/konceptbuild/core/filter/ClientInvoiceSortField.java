package com.konceptbuild.core.filter;

public enum ClientInvoiceSortField {
    DOCUMENT_NUMBER("documentNumber"),
    CLIENT_NAME("clientName"),
    WORK_NAME("workName"),
    DESCRIPTION("description"),
    VALUE_WITHOUT_TAX("valueWithoutTax"),
    APPLIED_TAX("appliedTax"),
    TAX_VALUE("taxValue"),
    TOTAL_VALUE("totalValue"),
    REGISTRATION_DATE("registrationDate"),
    DUE_DATE("dueDate");

    private final String fieldName;

    ClientInvoiceSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
