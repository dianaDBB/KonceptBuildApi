package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "wage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false, foreignKey = @ForeignKey(name = "fk_worker"))
    private WorkerEntity worker;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timesheet_id", nullable = false, foreignKey = @ForeignKey(name = "fk_timesheet"))
    private TimesheetEntity timesheet;

    @Column(name = "expected_wage", precision = 10, scale = 2)
    private Double expectedWage;

    @Column(name = "expected_extra_hours", precision = 10, scale = 2)
    private Double expectedExtraHours;

    @Column(name = "expected_deductions", precision = 10, scale = 2)
    private Double expectedDeductions;

    @Column(name = "expected_internal_cost", precision = 10, scale = 2)
    private Double expectedInternalCost;

    @Column(name = "paid_value", precision = 10, scale = 2)
    private Double paidValue;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "notes")
    private String notes;
}
