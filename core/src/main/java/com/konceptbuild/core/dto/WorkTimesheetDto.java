package com.konceptbuild.core.dto;

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
}
