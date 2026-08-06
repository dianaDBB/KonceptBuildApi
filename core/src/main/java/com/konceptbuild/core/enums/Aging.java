package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Aging {
    ZERO_THIRTY("ZERO_THIRTY", "0-30"),
    THIRTY_SIXTY("THIRTY_SIXTY", "31-60"),
    SIXTY_NINTY("SIXTY_NINTY", "61-90"),
    NINTY_PLUS("NINTY_PLUS", "Mais 90"),
    NOT_YET_DUE("NOT_YET_DUE", "A vencer"),
    PAID("PAID", "Pago"),
    NA("NA", "N/A");

    private final String code;
    private final String label;
}
