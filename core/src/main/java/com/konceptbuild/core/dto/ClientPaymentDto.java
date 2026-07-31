package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.ClientPaymentEntity;
import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.enums.ClientPaymentType;
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
public class ClientPaymentDto {
    private UUID id;
    private String documentId;
    private ClientPaymentType type;
    private ClientDto client;
    private LocalDate paymentDate;
    private Double paidValue;
    private PaymentMethod paymentMethod;
    private String notes;

    public ClientPaymentDto(ClientPaymentEntity entity) {
        this.id = entity.getId();
        this.documentId = entity.getDocumentId();
        this.type = entity.getType();
        this.client = new ClientDto(entity.getClient());
        this.paymentDate = entity.getPaymentDate();
        this.paidValue = entity.getPaidValue();
        this.paymentMethod = entity.getPaymentMethod();
        this.notes = entity.getNotes();
    }
}
