package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.WorkerService;
import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.request.WorkerRequest;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all workers")
    @ApiResponse(responseCode = "200", description = "Workers list retrieved successfully")
    public ResponseEntity<List<WorkerDto>> search(@RequestBody WorkerFilter request) {
        return ResponseEntity.ok(workerService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new worker")
    @ApiResponse(responseCode = "200", description = "Worker added successfully")
    public ResponseEntity<Void> add(@Valid @RequestBody WorkerRequest request) {
        workerService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing worker")
    @ApiResponse(responseCode = "200", description = "Worker edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody WorkerRequest request) {
        workerService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Delete an existing worker")
    @ApiResponse(responseCode = "200", description = "Worker deleted successfully")
    public ResponseEntity<Void> update(@PathVariable UUID id) {
        workerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
