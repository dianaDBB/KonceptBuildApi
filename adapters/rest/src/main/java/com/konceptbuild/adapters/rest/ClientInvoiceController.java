package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.ClientInvoiceService;
import com.konceptbuild.core.dto.ClientInvoiceDto;
import com.konceptbuild.core.filter.ClientInvoiceFilter;
import com.konceptbuild.core.request.CreateClientInvoiceRequest;
import com.konceptbuild.core.request.UpdateClientInvoiceRequest;
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
@RequestMapping("/client-invoice")
@SecurityRequirement(name = "bearerAuth")
public class ClientInvoiceController {
    @Autowired
    private ClientInvoiceService clientInvoiceService;

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all client invoices")
    @ApiResponse(responseCode = "200", description = "Invoices list retrieved successfully")
    public ResponseEntity<List<ClientInvoiceDto>> search(@RequestBody ClientInvoiceFilter request) {
        return ResponseEntity.ok(clientInvoiceService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new client invoice")
    @ApiResponse(responseCode = "200", description = "Invoice added successfully")
    public ResponseEntity<Void> create(@RequestBody CreateClientInvoiceRequest request) {
        clientInvoiceService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing invoice")
    @ApiResponse(responseCode = "200", description = "Invoice edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateClientInvoiceRequest request) {
        clientInvoiceService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Delete an existing invoice")
    @ApiResponse(responseCode = "200", description = "Invoice deleted successfully")
    public ResponseEntity<Void> update(@PathVariable UUID id) {
        clientInvoiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
