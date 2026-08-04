package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.ClientPaymentDto;
import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.enums.ClientPaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    @Column(name = "code_number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer codeNumber;

    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    @Column(name = "document_id", nullable = false, unique = true, insertable = false, updatable = false)
    private String documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ClientPaymentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "total_paid_value", nullable = false, precision = 10, scale = 2)
    private Double totalPaidValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "notes")
    private String notes;

    public ClientPaymentEntity(ClientPaymentDto dto) {
        this.id = dto.getId();
        this.documentId = dto.getDocumentId();
        this.type = dto.getType();
        this.client = new ClientEntity(dto.getClient());
        this.paymentDate = dto.getPaymentDate();
        this.totalPaidValue = dto.getTotalPaidValue();
        this.paymentMethod = dto.getPaymentMethod();
        this.notes = dto.getNotes();
    }
}
