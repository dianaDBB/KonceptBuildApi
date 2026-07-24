package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkerContractType {
    INTERNAL("INTERNAL", "Contrato"),
    CONTRACTOR("CONTRACTOR", "Hora");

    private final String code;
    private final String label;
}
