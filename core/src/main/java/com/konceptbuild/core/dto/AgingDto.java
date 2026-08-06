package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record AgingDto(
        String code,
        String label
) {
}