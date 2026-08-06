package com.konceptbuild.core.filter;

public enum ClientInvoiceSortField {
    DOCUMENT_NUMBER("docNumber"),
    CLIENT("client.code"),
    WORK("work.code"),
    DESCRIPTION("description"),
    VALUE_WITHOUT_TAX("valueWithoutTax"),
    APPLIED_TAX("appliedTax"),
    TAX_VALUE("taxValue"),
    TOTAL_VALUE("totalValue"),
    REGISTRATION_DATE("registrationDate"),
    DUE_DATE("dueDate"),
    SUM_CREDIT_NOTES_WITHOUT_TAX("sumCreditNotesWithoutTax"),
    SUM_CREDIT_NOTES_WITH_TAX("sumCreditNotesWithTax"),
    TOTAL_VALUE_NET("totalValueNet"),
    TOTAL_VALUE_GROSS("totalValueGross"),
    AMOUNT_RECEIVED_WITHOUT_TAX("amountReceivedWithoutTax"),
    AMOUNT_RECEIVED_WITH_TAX("amountReceivedWithTax"),
    AMOUNT_DUE_WITHOUT_TAX("amountDueWithoutTax"),
    AMOUNT_DUE_WITH_TAX("amountDueWithTax"),
    PAYMENTS_COUNT("paymentsCount"),
    STATUS("status"),
    DAYS_PAST_DUE("daysPastDue"),
    AGING("aging"),
    SETTLEMENT_DATE("settlementDate"),
    DAYS_TO_PAY("daysToPay");

    private final String fieldName;

    ClientInvoiceSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
