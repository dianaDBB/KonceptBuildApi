package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WorkerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDto {
    private UUID id;
    private String name;
    private WorkerType workerType;
    private Double hourRate;
    private Double monthlySalary;
    private Double hourCost;

    public WorkerDto(WorkerEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.workerType = entity.getWorkerType();
        this.hourRate = entity.getHourRate();
        this.monthlySalary = entity.getMonthlySalary();
        this.hourCost = entity.getHourCost();
    }
}
