package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
    PAID("PAID", "Pago"),
    PARTIAL("PARTIAL", "Parcial"),
    DELAY("DELAY", "Em Atraso"),
    PENDING("PENDING", "Pendente");

    private final String code;
    private final String label;
}
