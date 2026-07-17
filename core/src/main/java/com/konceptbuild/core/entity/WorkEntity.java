package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.request.WorkRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "work")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code_number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer codeNumber;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code", nullable = false, unique = true, insertable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkStatus status;

    @Column(name = "contracted_budget", nullable = false)
    private Double contractedBudget;

    @Column(name = "estimated_cost_materials", nullable = false)
    private Double estimatedCostMaterials;

    @Column(name = "estimated_cost_labor", nullable = false)
    private Double estimatedCostLabor;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "estimated_end_date", nullable = false)
    private LocalDate estimatedEndDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "client_id")
    private UUID clientId;


    public WorkEntity(WorkRequest request) {
        this.id = request.id();
        this.name = request.name();
        this.status = request.status();
        this.contractedBudget = request.contractedBudget();
        this.estimatedCostMaterials = request.estimatedCostMaterials();
        this.estimatedCostLabor = request.estimatedCostLabor();
        this.startDate = request.startDate();
        this.estimatedEndDate = request.estimatedEndDate();
        this.endDate = request.endDate();
        this.clientId = request.clientId();
    }
}
