package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.TimesheetEntryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayEntryDto {
    private LocalDate date;
    private Double hours;

    public DayEntryDto(TimesheetEntryEntity entity) {
        this.date = entity.getDate();
        this.hours = entity.getHours();
    }
}
