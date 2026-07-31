package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.*;
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
public class ClientInvoiceDto {
    private UUID id;
    private String docNumber;
    private ClientDto client;
    private WorkDto work;
    private String description;
    private Double valueWithoutTax;
    private Double appliedTax;
    private Double taxValue;
    private Double totalValue;
    private LocalDate registrationDate;
    private LocalDate dueDate;

    public ClientInvoiceDto(ClientInvoiceEntity entity) {
        this.id = entity.getId();
        this.docNumber = entity.getDocNumber();
        this.client = new ClientDto(entity.getClient());
        this.work = new WorkDto(entity.getWork());
        this.description = entity.getDescription();
        this.valueWithoutTax = entity.getValueWithoutTax();
        this.appliedTax = entity.getAppliedTax();
        this.taxValue = entity.getTaxValue();
        this.totalValue = entity.getTotalValue();
        this.registrationDate = entity.getRegistrationDate();
        this.dueDate = entity.getDueDate();
    }
}
