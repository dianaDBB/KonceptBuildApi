package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record StatusDto(
        String code,
        String label
) {
}