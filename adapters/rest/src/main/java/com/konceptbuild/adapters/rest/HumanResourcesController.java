package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.HumanResourcesService;
import com.konceptbuild.core.dto.HrDashboardDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/hr")
@SecurityRequirement(name = "bearerAuth")
public class HumanResourcesController {
    @Autowired
    HumanResourcesService humanResourcesService;

    @GetMapping("/")
    @Operation(description = "Returns the HR dashboard")
    @ApiResponse(responseCode = "200", description = "HR dashboard retrieved successfully")
    public HrDashboardDto getDashboard(@RequestParam(required = false) Integer year,
                                       @RequestParam(required = false) Integer month) {
        return humanResourcesService.getDashboard(year, month);
    }
}
