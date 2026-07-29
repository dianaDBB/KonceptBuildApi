package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record PaymentMethodDto(
        String code,
        String label
) {
}