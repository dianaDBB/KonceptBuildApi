package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.WorkService;
import com.konceptbuild.core.dto.WorkDto;
import com.konceptbuild.core.filter.WorkFilter;
import com.konceptbuild.core.request.WorkRequest;
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
@RequestMapping("/work")
@SecurityRequirement(name = "bearerAuth")
public class WorkController {
    @Autowired
    WorkService workService;

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all works")
    @ApiResponse(responseCode = "200", description = "Works list retrieved successfully")
    public ResponseEntity<List<WorkDto>> search(@RequestBody WorkFilter request) {
        return ResponseEntity.ok(workService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new work")
    @ApiResponse(responseCode = "200", description = "Work added successfully")
    public ResponseEntity<Void> add(@Valid @RequestBody WorkRequest request) {
        workService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing work")
    @ApiResponse(responseCode = "200", description = "Work edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody WorkRequest request) {
        workService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Delete an existing work")
    @ApiResponse(responseCode = "200", description = "Work deleted successfully")
    public ResponseEntity<Void> update(@PathVariable UUID id) {
        workService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
