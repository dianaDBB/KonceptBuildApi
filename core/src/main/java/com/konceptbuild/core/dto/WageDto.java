package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WageEntity;
import com.konceptbuild.core.enums.PaymentMethod;
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
public class WageDto {
    private UUID id;
    private String code;
    private Integer year;
    private Integer month;
    private WorkerDto workerDto;
    private WorkerTimesheetDto workerTimesheetDto;
    private Double expectedWage;
    private Double expectedExtraHours;
    private Double expectedDeductions;
    private Double expectedInternalCost;
    private Double paidValue;
    private LocalDate paidDate;
    private PaymentMethod paymentMethod;
    private String notes;

    public WageDto(WageEntity entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.year = entity.getYear();
        this.month = entity.getMonth();
        this.workerDto = new WorkerDto(entity.getWorker(), new WorkerHistoryDto(entity.getWorkerHistory()));
        this.workerTimesheetDto = new WorkerTimesheetDto(entity.getTimesheet());
        this.expectedWage = entity.getExpectedWage();
        this.expectedExtraHours = entity.getExpectedExtraHours();
        this.expectedDeductions = entity.getExpectedDeductions();
        this.expectedInternalCost = entity.getExpectedInternalCost();
        this.paidValue = entity.getPaidValue();
        this.paidDate = entity.getPaidDate();
        this.paymentMethod = entity.getPaymentMethod();
        this.notes = entity.getNotes();
    }
}
