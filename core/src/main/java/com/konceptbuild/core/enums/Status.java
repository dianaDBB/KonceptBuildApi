package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("ACTIVE", "Ativo"),
    INACTIVE("INACTIVE", "Inativo");

    private final String code;
    private final String label;
}
