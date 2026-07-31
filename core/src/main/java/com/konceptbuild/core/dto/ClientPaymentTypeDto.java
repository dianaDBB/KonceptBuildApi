package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record ClientPaymentTypeDto(
        String code,
        String label
) {
}