package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.TimesheetLineEntity;
import com.konceptbuild.core.enums.AttendanceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimesheetDto {
    WorkDto work;
    AttendanceCode attendanceCode;
    List<DayEntryDto> days;

    public WorkTimesheetDto(TimesheetLineEntity entity) {
        if (entity.getWork() != null) {
            this.work = new WorkDto(entity.getWork());
        }

        if (entity.getAttendanceCode() != null) {
            this.attendanceCode = entity.getAttendanceCode();
        }

        this.days = entity.getEntries().stream().map((DayEntryDto::new)).toList();
    }
}
