package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.WorkerService;
import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerFilter;
import com.konceptbuild.core.dto.WorkerSortField;
import com.konceptbuild.core.dto.SortDirection;
import com.konceptbuild.core.dto.WorkerType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/worker")
@SecurityRequirement(name = "bearerAuth")
public class WorkerController {
    @Autowired
    WorkerService workerService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all workers")
    @ApiResponse(responseCode = "200", description = "Workers list retrieved successfully")
    public ResponseEntity<List<WorkerDto>> getAll(
            @RequestParam(name = "id", required = false) UUID id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "workerType", required = false) WorkerType workerType,
            @RequestParam(name = "minHourRate", required = false) Double minHourRate,
            @RequestParam(name = "maxHourRate", required = false) Double maxHourRate,
            @RequestParam(name = "minMonthlySalary", required = false) Double minMonthlySalary,
            @RequestParam(name = "maxMonthlySalary", required = false) Double maxMonthlySalary,
            @RequestParam(name = "minHourCost", required = false) Double minHourCost,
            @RequestParam(name = "maxHourCost", required = false) Double maxHourCost,
            @RequestParam(name = "sortBy", defaultValue = "NAME") WorkerSortField sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") SortDirection sortDirection) {
        try {
            WorkerFilter filter = new WorkerFilter(id, name, workerType, minHourRate, maxHourRate,
                    minMonthlySalary, maxMonthlySalary, minHourCost, maxHourCost, sortBy, sortDirection);
            return ResponseEntity.ok(workerService.getAll(filter));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new worker")
    @ApiResponse(responseCode = "200", description = "Worker added successfully")
    public ResponseEntity<Void> add(@RequestBody WorkerDto request) {
        workerService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
