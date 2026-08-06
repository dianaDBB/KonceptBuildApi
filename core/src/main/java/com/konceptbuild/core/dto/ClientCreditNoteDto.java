package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.ClientCreditNoteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreditNoteDto {
    private UUID id;
    private String docNumber;
    private String description;
    private Double valueWithoutTax;
    private Double appliedTax;
    private Double taxValue;
    private Double totalValue;
    private LocalDate registrationDate;
    private LocalDate dueDate;

    public ClientCreditNoteDto(ClientCreditNoteEntity entity) {
        this.id = entity.getId();
        this.docNumber = entity.getDocNumber();
        this.description = entity.getDescription();
        this.valueWithoutTax = entity.getValueWithoutTax();
        this.appliedTax = entity.getAppliedTax();
        this.taxValue = entity.getTaxValue();
        this.totalValue = entity.getTotalValue();
        this.registrationDate = entity.getRegistrationDate();
        this.dueDate = entity.getDueDate();
    }
}
