package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WorkerHistoryEntity;
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
public class WorkerHistoryDto {
    private UUID id;
    private UUID workerId;
    private Double hourCost;
    private Double defaultHours;
    private Double hourRate;
    private Double monthlySalary;
    private Double tsu;
    private Double mealAllowance;
    private Double accidentInsurance;
    private LocalDate validFrom;
    private LocalDate validTo;

    public WorkerHistoryDto(WorkerHistoryEntity entity) {
        this.id = entity.getId();
        this.workerId = entity.getWorker().getId();
        this.hourCost = entity.getHourCost();
        this.defaultHours = entity.getDefaultHours();
        this.hourRate = entity.getHourRate();
        this.monthlySalary = entity.getMonthlySalary();
        this.tsu = entity.getTsu();
        this.mealAllowance = entity.getMealAllowance();
        this.accidentInsurance = entity.getAccidentInsurance();
        this.validFrom = entity.getValidFrom();
        this.validTo = entity.getValidTo();
    }
}
