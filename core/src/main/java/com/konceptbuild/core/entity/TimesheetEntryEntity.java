package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.AttendanceCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "timesheet_entry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timesheet_id", nullable = false, foreignKey = @ForeignKey(name = "fk_timesheet"))
    private TimesheetEntity timesheet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false, foreignKey = @ForeignKey(name = "fk_work"))
    private WorkEntity work;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "hours", nullable = false, precision = 3, scale = 1)
    private Double hours;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_code")
    private AttendanceCode attendanceCode;
}
