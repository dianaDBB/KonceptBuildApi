package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkStatus {
    STARTED("STARTED", "Iniciada"),
    IN_PROGRESS("IN_PROGRESS", "Em Curso"),
    DONE("DONE", "Concluída"),
    SUSPENDED("SUSPENDED", "Suspensa");

    private final String code;
    private final String label;
}
