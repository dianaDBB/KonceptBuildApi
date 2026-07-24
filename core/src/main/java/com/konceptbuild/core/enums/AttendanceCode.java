package com.konceptbuild.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceCode {
    VACATION("VACATION", "Férias", true),
    SIK_LEAVE("SIK_LEAVE", "Baixa Médica", false),
    PARENTAL_LEAVE("PARENTAL_LEAVE", "Licença Parental", false),
    UNJUSTIFIED_ABSENCE("UNJUSTIFIED_ABSENCE", "Falta Injustificada", false),
    JUSTIFIED_ABSENCE_PAID("JUSTIFIED_ABSENCE_PAID", "Falta Justificada (paga)", true),
    JUSTIFIED_ABSENCE_UNPAID("JUSTIFIED_ABSENCE_UNPAID", "Falta Justificada (não paga)", false);

    private final String code;
    private final String label;
    private final boolean isPaid;
}
