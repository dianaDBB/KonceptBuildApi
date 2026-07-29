package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CASH("CASH", "Numerário"),
    TRANSFER("TRANSFER", "Transferência"),
    BANK_CHECK("BANK_CHECK", "Cheque"),
    MB_WAY("MB_WAY", "MB Way"),
    DIRECT_DEBIT("DIRECT_DEBIT", "Débito Direto");

    private final String code;
    private final String label;
}
