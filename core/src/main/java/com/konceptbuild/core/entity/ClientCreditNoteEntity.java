package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.ClientCreditNoteDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client_credit_note")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreditNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "doc_number", nullable = false)
    private String docNumber;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "value_without_tax", nullable = false, precision = 10, scale = 2)
    private Double valueWithoutTax;

    @Column(name = "applied_tax", nullable = false, precision = 5, scale = 2)
    private Double appliedTax;

    @Column(name = "tax_value", nullable = false, precision = 10, scale = 2)
    private Double taxValue;

    @Column(name = "total_value", nullable = false, precision = 10, scale = 2)
    private Double totalValue;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "client_invoice_id")
    ClientInvoiceEntity clientInvoice;

    public ClientCreditNoteEntity(ClientCreditNoteDto dto) {
        this.id = dto.getId();
        this.docNumber = dto.getDocNumber();
        this.description = dto.getDescription();
        this.valueWithoutTax = dto.getValueWithoutTax();
        this.appliedTax = dto.getAppliedTax();
        this.taxValue = dto.getTaxValue();
        this.totalValue = dto.getTotalValue();
        this.registrationDate = dto.getRegistrationDate();
        this.dueDate = dto.getDueDate();
    }
}
