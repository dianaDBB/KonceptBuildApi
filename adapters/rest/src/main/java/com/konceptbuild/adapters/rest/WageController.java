package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.WageService;
import com.konceptbuild.core.dto.WageDto;
import com.konceptbuild.core.filter.WageFilter;
import com.konceptbuild.core.request.AddWageRequest;
import com.konceptbuild.core.request.UpdateWageRequest;
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

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/wage")
@SecurityRequirement(name = "bearerAuth")
public class WageController {
    @Autowired
    WageService wageService;

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all wages")
    @ApiResponse(responseCode = "200", description = "Wages list retrieved successfully")
    public ResponseEntity<List<WageDto>> search(@RequestBody WageFilter request) {
        return ResponseEntity.ok(wageService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new wage based on a timesheet")
    @ApiResponse(responseCode = "200", description = "Wage added successfully")
    public ResponseEntity<Void> add(@Valid @RequestBody AddWageRequest request) {
        wageService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing wage")
    @ApiResponse(responseCode = "200", description = "Wage edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateWageRequest request) {
        wageService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
