package com.konceptbuild.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "timesheet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false, foreignKey = @ForeignKey(name = "fk_worker"))
    private WorkerEntity worker;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "total_extra_hours")
    private Double totalExtraHours;

    @Column(name = "total_paid_absence_hours")
    private Double totalPaidAbsenceHours;

    @Column(name = "total_unpaid_absence_hours")
    private Double totalUnpaidAbsenceHours;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "total_cost_extra_hours")
    private Double totalCostExtraHours;

    @Column(name = "total_cost_unpaid_absence_hours")
    private Double totalCostUnpaidAbsenceHours;

    @OneToMany(
            mappedBy = "timesheet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TimesheetLineEntity> timesheetLineEntities;
}
