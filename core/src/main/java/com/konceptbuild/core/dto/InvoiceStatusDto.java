package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record InvoiceStatusDto(
        String code,
        String label
) {
}