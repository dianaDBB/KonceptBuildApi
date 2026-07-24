package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.ConfigsService;
import com.konceptbuild.core.dto.AttendanceCodeDto;
import com.konceptbuild.core.dto.StatusDto;
import com.konceptbuild.core.dto.WorkStatusDto;
import com.konceptbuild.core.dto.WorkerContractTypeDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/configs")
@SecurityRequirement(name = "bearerAuth")
public class ConfigsController {
    @Autowired
    ConfigsService configsService;

    @GetMapping("/status")
    @Operation(description = "Returns all possible status")
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public List<StatusDto> getStatus() {
        return configsService.getStatus();
    }

    @GetMapping("/worker-contract-type")
    @Operation(description = "Returns all possible worker contract types")
    @ApiResponse(responseCode = "200", description = "Worker contract types retrieved successfully")
    public List<WorkerContractTypeDto> getWorkerContractTypes() {
        return configsService.getWorkerContractType();
    }

    @GetMapping("/work-status")
    @Operation(description = "Returns all possible work status")
    @ApiResponse(responseCode = "200", description = "Work status retrieved successfully")
    public List<WorkStatusDto> getWorkStatus() {
        return configsService.getWorkStatus();
    }

    @GetMapping("/attendance-codes")
    @Operation(description = "Returns all possible attendance codes")
    @ApiResponse(responseCode = "200", description = "Attendance codes retrieved successfully")
    public List<AttendanceCodeDto> getAttendanceCodes() {
        return configsService.getAttendanceCodes();
    }
}
