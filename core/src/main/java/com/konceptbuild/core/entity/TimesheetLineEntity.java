package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.AttendanceCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "timesheet_line")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id")
    private TimesheetEntity timesheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")
    private WorkEntity work;

    @Enumerated(EnumType.STRING)
    private AttendanceCode attendanceCode;

    @OneToMany(
            mappedBy = "line",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TimesheetEntryEntity> entries = new ArrayList<>();
}
