package com.konceptbuild.core.filter;

import com.konceptbuild.core.dto.Status;

public record ClientFilter(
        String code,
        String companyName,
        String address,
        String postalCode,
        String city,
        String district,
        String nif,
        String contact,
        String email,
        String phone,
        Status status,
        String note,

        ClientSortField sortBy,
        SortDirection sortDirection) {

    public ClientFilter {
        sortBy = sortBy == null ? ClientSortField.COMPANY_NAME : sortBy;
        sortDirection = sortDirection == null ? SortDirection.ASC : sortDirection;
    }
}
