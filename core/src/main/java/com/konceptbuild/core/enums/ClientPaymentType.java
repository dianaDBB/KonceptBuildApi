package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientPaymentType {
    PAYMENT("PAYMENT", "Pagamento"),
    REFUND("REFUND", "Reembolso");

    private final String code;
    private final String label;
}
