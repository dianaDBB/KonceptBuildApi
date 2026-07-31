package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.ClientPaymentService;
import com.konceptbuild.core.dto.ClientPaymentDto;
import com.konceptbuild.core.filter.ClientPaymentFilter;
import com.konceptbuild.core.request.CreateClientPaymentRequest;
import com.konceptbuild.core.request.UpdateClientPaymentRequest;
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
@RequestMapping("/client-payment")
@SecurityRequirement(name = "bearerAuth")
public class ClientPaymentController {
    @Autowired
    ClientPaymentService clientPaymentService;

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all payments")
    @ApiResponse(responseCode = "200", description = "Payments list retrieved successfully")
    public ResponseEntity<List<ClientPaymentDto>> search(@RequestBody ClientPaymentFilter request) {
        return ResponseEntity.ok(clientPaymentService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new client payment")
    @ApiResponse(responseCode = "200", description = "Payment added successfully")
    public ResponseEntity<Void> create(@RequestBody CreateClientPaymentRequest request) {
        clientPaymentService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing payment")
    @ApiResponse(responseCode = "200", description = "Payment edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateClientPaymentRequest request) {
        clientPaymentService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Delete an existing payment")
    @ApiResponse(responseCode = "200", description = "Payment deleted successfully")
    public ResponseEntity<Void> update(@PathVariable UUID id) {
        clientPaymentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
