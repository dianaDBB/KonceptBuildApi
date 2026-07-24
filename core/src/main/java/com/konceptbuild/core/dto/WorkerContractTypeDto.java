package com.konceptbuild.core.dto;

import lombok.Builder;

@Builder
public record WorkerContractTypeDto(
        String code,
        String label
) {
}