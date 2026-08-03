package com.konceptbuild.core.filter;

public enum ClientSortField {
    CODE("code"),
    COMPANY_NAME("companyName"),
    ADDRESS("address"),
    POSTAL_CODE("postalCode"),
    CITY("city"),
    DISTRICT("district"),
    NIF("nif"),
    CONTACT("contact"),
    EMAIL("email"),
    PHONE("phone"),
    STATUS("status"),
    NOTE("note");

    private final String fieldName;

    ClientSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
