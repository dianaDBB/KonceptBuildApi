package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.TimesheetService;
import com.konceptbuild.core.dto.MonthlyTimesheetDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/timesheet")
@SecurityRequirement(name = "bearerAuth")
public class TimesheetController {
    @Autowired
    TimesheetService timesheetService;

    @GetMapping("/monthly")
    @Operation(description = "Returns the timesheet")
    @ApiResponse(responseCode = "200", description = "Timesheet retrieved successfully")
    public MonthlyTimesheetDto getMonthlyTimesheet(@RequestParam int year, @RequestParam int month) {
        return timesheetService.getMonthlyTimesheet(year, month);
    }

    @PutMapping(value = "/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing timesheet")
    @ApiResponse(responseCode = "200", description = "Timesheet edited successfully")
    public void save(@RequestBody @Valid MonthlyTimesheetDto dto) {
        timesheetService.saveMonthlyTimesheet(dto);
    }


}
