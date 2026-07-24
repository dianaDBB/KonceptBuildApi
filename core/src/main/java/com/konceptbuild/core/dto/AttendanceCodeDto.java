package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record AttendanceCodeDto(
        String code,
        String label,
        boolean paid
) {
}