package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.request.WorkerRequest;
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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nif", nullable = false, unique = true)
    private String nif;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "phone_country_code", nullable = false)
    private String phoneCountryCode;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "function", nullable = false)
    private String function;

    @Column(name = "hour_cost", precision = 10, scale = 1)
    private Double hourCost;

    @Column(name = "default_hours", precision = 10, scale = 1)
    private Double defaultHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private WorkerContractType workerContractType;

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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public WorkerEntity(WorkerRequest request) {
        this.id = request.id();
        this.name = request.name();
        this.nif = request.nif();
        this.status = request.status();
        this.phoneCountryCode = request.phoneCountryCode();
        this.phone = request.phone();
        this.email = request.email();
        this.function = request.function();
        this.defaultHours = request.defaultHours();
        this.workerContractType = request.workerContractType();
        this.hourRate = request.hourRate();
        this.monthlySalary = request.monthlySalary();
        this.tsu = request.tsu();
        this.mealAllowance = request.mealAllowance();
        this.accidentInsurance = request.accidentInsurance();
        this.startDate = request.startDate();
        this.endDate = request.endDate();

        this.hourCost = switch (request.workerContractType()) {
            case CONTRACTOR -> {
                yield request.hourRate();
            }

            case INTERNAL -> {
                var tsu = (request.monthlySalary() * (request.tsu() / 100)) * 14 / 12;
                var mealAllowance = (request.mealAllowance() * 22) * 11 / 12;
                var accidentInsurance = request.accidentInsurance();
                var monthlyCost = (request.monthlySalary() * 14 / 12) + tsu + mealAllowance + accidentInsurance;
                yield monthlyCost / 22 / request.defaultHours();
            }
        };
    }
}
