package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.enums.WorkerContractType;
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
    private String nif;
    private Status status;
    private String phone;
    private String phoneCountryCode;
    private String email;
    private String function;
    private Double hourCost;
    private Double defaultHours;
    private WorkerContractType workerContractType;
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
        this.nif = entity.getNif();
        this.status = entity.getStatus();
        this.phoneCountryCode = entity.getPhoneCountryCode();
        this.phone = entity.getPhone();
        this.email = entity.getEmail();
        this.function = entity.getFunction();
        this.hourCost = entity.getHourCost();
        this.defaultHours = entity.getDefaultHours();
        this.workerContractType = entity.getWorkerContractType();
        this.hourRate = entity.getHourRate();
        this.monthlySalary = entity.getMonthlySalary();
        this.tsu = entity.getTsu();
        this.mealAllowance = entity.getMealAllowance();
        this.accidentInsurance = entity.getAccidentInsurance();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
    }

    public boolean isActiveDuringPeriod(LocalDate startDate, LocalDate endDate, LocalDate periodStart,
                                        LocalDate periodEnd) {
        if (startDate == null) {
            return false;
        }

        return !startDate.isAfter(periodEnd) && (endDate == null || !endDate.isBefore(periodStart));
    }
}
