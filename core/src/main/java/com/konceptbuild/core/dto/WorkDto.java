package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WorkEntity;
import com.konceptbuild.core.enums.WorkStatus;
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
public class WorkDto {
    private UUID id;
    private Integer codeNumber;
    private String code;
    private String name;
    private WorkStatus status;
    private Double contractedBudget;
    private Double estimatedCost;
    private Double estimatedCostMaterials;
    private Double estimatedCostLabor;
    private Double estimatedMarginEur;
    private Double estimatedMarginPercentual;
    private LocalDate startDate;
    private LocalDate estimatedEndDate;
    private LocalDate endDate;
    private ClientDto client;

    public WorkDto(WorkEntity entity, ClientDto client) {
        this.id = entity.getId();
        this.codeNumber = entity.getCodeNumber();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.status = entity.getStatus();
        this.contractedBudget = entity.getContractedBudget();
        this.estimatedCostMaterials = entity.getEstimatedCostMaterials();
        this.estimatedCostLabor = entity.getEstimatedCostLabor();
        this.startDate = entity.getStartDate();
        this.estimatedEndDate = entity.getEstimatedEndDate();
        this.endDate = entity.getEndDate();
        this.client = client;

        this.estimatedCost = this.estimatedCostMaterials + this.estimatedCostLabor;
        this.estimatedMarginEur = this.contractedBudget - this.estimatedCost;
        this.estimatedMarginPercentual = this.estimatedMarginEur / this.contractedBudget * 100;
    }

    public WorkDto(WorkEntity entity) {
        this.id = entity.getId();
        this.codeNumber = entity.getCodeNumber();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.status = entity.getStatus();
        this.contractedBudget = entity.getContractedBudget();
        this.estimatedCostMaterials = entity.getEstimatedCostMaterials();
        this.estimatedCostLabor = entity.getEstimatedCostLabor();
        this.startDate = entity.getStartDate();
        this.estimatedEndDate = entity.getEstimatedEndDate();
        this.endDate = entity.getEndDate();

        this.estimatedCost = this.estimatedCostMaterials + this.estimatedCostLabor;
        this.estimatedMarginEur = this.contractedBudget - this.estimatedCost;
        this.estimatedMarginPercentual = this.estimatedMarginEur / this.contractedBudget * 100;
    }
}
