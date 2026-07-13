package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerStatus;
import com.konceptbuild.core.dto.ContractType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "worker")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code_number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer codeNumber;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code", nullable = false, unique = true, insertable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkerStatus status;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "function", nullable = false)
    private String function;

    @Column(name = "default_hours", precision = 10, scale = 1)
    private Double defaultHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    @Column(name = "hour_rate", precision = 10, scale = 2)
    private Double hourRate;

    @Column(name = "monthly_salary", precision = 10, scale = 2)
    private Double monthlySalary;

    @Column(name = "tsu", precision = 10, scale = 2)
    private Double tsu;

    @Column(name = "meal_allowance", precision = 10, scale = 2)
    private Double mealAllowance;

    @Column(name = "accident_insurance", precision = 10, scale = 2)
    private Double accidentInsurance;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public WorkerEntity(WorkerDto dto) {
        this.id = dto.getId();
        this.codeNumber = dto.getCodeNumber();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.contact = dto.getContact();
        this.email = dto.getEmail();
        this.function = dto.getFunction();
        this.defaultHours = dto.getDefaultHours();
        this.contractType = dto.getContractType();
        this.hourRate = dto.getHourRate();
        this.monthlySalary = dto.getMonthlySalary();
        this.tsu = dto.getTsu();
        this.mealAllowance = dto.getMealAllowance();
        this.accidentInsurance = dto.getAccidentInsurance();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
    }
}
