package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerType;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "worker_type")
    private WorkerType workerType;

    @Column(name = "hour_rate")
    private Double hourRate;

    @Column(name = "monthly_salary")
    private Double monthlySalary;

    @Column(name = "hour_cost")
    private Double hourCost;

    public WorkerEntity(WorkerDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.workerType = dto.getWorkerType();
        this.hourRate = dto.getHourRate();
        this.monthlySalary = dto.getMonthlySalary();

        this.hourCost = switch (dto.getWorkerType()) {
            case WorkerType.CONTRACTOR -> dto.getHourRate() * 1.23;

            case INTERNAL -> {
                double ss = dto.getMonthlySalary() * 0.2375;
                double monthlyCost = (dto.getMonthlySalary() * 14 / 12) + (ss * 14 / 12);
                yield monthlyCost / 22 / 8;
            }
        };
    }
}
