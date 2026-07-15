package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.ClientService;
import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.filter.ClientFilter;
import com.konceptbuild.core.request.ClientRequest;
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
@RequestMapping("/client")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {
    @Autowired
    ClientService clientService;

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Returns a list of all clients")
    @ApiResponse(responseCode = "200", description = "Clients list retrieved successfully")
    public ResponseEntity<List<ClientDto>> search(@RequestBody ClientFilter request) {
        return ResponseEntity.ok(clientService.search(request));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Add a new client")
    @ApiResponse(responseCode = "200", description = "Client added successfully")
    public ResponseEntity<Void> add(@Valid @RequestBody ClientRequest request) {
        clientService.add(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Edit an existing client")
    @ApiResponse(responseCode = "200", description = "Client edited successfully")
    public ResponseEntity<Void> update(@Valid @RequestBody ClientRequest request) {
        clientService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Delete an existing client")
    @ApiResponse(responseCode = "200", description = "Client deleted successfully")
    public ResponseEntity<Void> update(@PathVariable UUID id) {
        clientService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
