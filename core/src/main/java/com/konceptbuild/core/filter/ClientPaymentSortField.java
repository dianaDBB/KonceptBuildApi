package com.konceptbuild.core.filter;

public enum ClientPaymentSortField {
    DOCUMENT_ID("documentId"),
    PAYMENT_TYPE("paymentType"),
    CLIENT("client.code"),
    PAYMENT_DATE("paymentDate"),
    PAID_VALUE("paidValue"),
    PAYMENT_METHOD("paymentMethod"),
    NOTES("notes");

    private final String fieldName;

    ClientPaymentSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
