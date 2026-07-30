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
    private WorkerContractType workerContractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private WorkerHistoryDto currentWorkerCompensation;

    public WorkerDto(WorkerEntity entity, WorkerHistoryDto currentWorkerHistoryDto) {
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
        this.workerContractType = entity.getWorkerContractType();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.currentWorkerCompensation = currentWorkerHistoryDto;
    }

    public boolean isActiveDuringPeriod(LocalDate startDate, LocalDate endDate, LocalDate periodStart,
                                        LocalDate periodEnd) {
        if (startDate == null) {
            return false;
        }

        return !startDate.isAfter(periodEnd) && (endDate == null || !endDate.isBefore(periodStart));
    }
}
