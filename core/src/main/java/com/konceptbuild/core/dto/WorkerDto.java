package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WorkerEntity;
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
public class WorkerDto {
    private UUID id;
    private Integer codeNumber;
    private String code;
    private String name;
    private WorkerStatus status;
    private String contact;
    private String email;
    private String function;
    private Double defaultHours;
    private ContractType contractType;
    private Double hourRate;
    private Double monthlySalary;
    private Double tsu;
    private Double mealAllowance;
    private Double accidentInsurance;
    private LocalDate startDate;
    private LocalDate endDate;

    public WorkerDto(WorkerEntity entity) {
        this.id = entity.getId();
        this.codeNumber = entity.getCodeNumber();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.status = entity.getStatus();
        this.contact = entity.getContact();
        this.email = entity.getEmail();
        this.function = entity.getFunction();
        this.defaultHours = entity.getDefaultHours();
        this.contractType = entity.getContractType();
        this.hourRate = entity.getHourRate();
        this.monthlySalary = entity.getMonthlySalary();
        this.tsu = entity.getTsu();
        this.mealAllowance = entity.getMealAllowance();
        this.accidentInsurance = entity.getAccidentInsurance();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
    }
}
