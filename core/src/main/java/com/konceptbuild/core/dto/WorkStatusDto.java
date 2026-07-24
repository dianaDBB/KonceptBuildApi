package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record WorkStatusDto(
        String code,
        String label
) {
}