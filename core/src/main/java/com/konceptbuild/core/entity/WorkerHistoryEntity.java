package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.WorkerHistoryDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "worker_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private WorkerEntity worker;

    @Column(name = "hour_cost", precision = 10, scale = 1)
    private Double hourCost;

    @Column(name = "default_hours", precision = 10, scale = 1)
    private Double defaultHours;

    @Column(name = "hour_rate", precision = 3, scale = 1)
    private Double hourRate;

    @Column(name = "monthly_salary", precision = 10, scale = 2)
    private Double monthlySalary;

    @Column(name = "tsu", precision = 10, scale = 2)
    private Double tsu;

    @Column(name = "meal_allowance", precision = 10, scale = 2)
    private Double mealAllowance;

    @Column(name = "accident_insurance", precision = 10, scale = 2)
    private Double accidentInsurance;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    public WorkerHistoryEntity(WorkerHistoryDto dto) {
        this.id = dto.getId();
        this.hourCost = dto.getHourCost();
        this.defaultHours = dto.getDefaultHours();
        this.hourRate = dto.getHourRate();
        this.monthlySalary = dto.getMonthlySalary();
        this.tsu = dto.getTsu();
        this.mealAllowance = dto.getMealAllowance();
        this.accidentInsurance = dto.getAccidentInsurance();
        this.validFrom = dto.getValidFrom();
        this.validTo = dto.getValidTo();
    }
}
