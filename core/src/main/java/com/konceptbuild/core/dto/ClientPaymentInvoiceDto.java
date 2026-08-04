package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.ClientPaymentInvoiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPaymentInvoiceDto {
    private ClientInvoiceDto invoice;
    private Double paidValue;

    public ClientPaymentInvoiceDto(ClientPaymentInvoiceEntity entity) {
        this.invoice = new ClientInvoiceDto(entity.getInvoice());
        this.paidValue = entity.getPaidValue();
    }
}
