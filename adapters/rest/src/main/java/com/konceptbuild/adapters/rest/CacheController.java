package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.CacheService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@OpenAPIDefinition(info = @Info(title = "Budget API", version = "1.0"))
@RequestMapping("/cache")
@SecurityRequirement(name = "bearerAuth")
public class CacheController {
    @Autowired
    CacheService cacheService;

    @PostMapping("/refresh")
    @Operation(description = "Reloads file configs and category rules from the database into memory")
    @ApiResponse(responseCode = "200", description = "Cache refreshed successfully")
    public ResponseEntity<Void> refreshCache() {
        cacheService.refreshCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
