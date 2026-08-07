package com.konceptbuild.adapters.rest;

import com.konceptbuild.core.SalesDashboardsService;
import com.konceptbuild.core.dto.SalesClientReportDto;
import com.konceptbuild.core.dto.SalesDashboardDto;
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

import java.util.UUID;

@RestController
@OpenAPIDefinition(info = @Info(title = "KonceptBuild API", version = "1.0"))
@RequestMapping("/sales")
@SecurityRequirement(name = "bearerAuth")
public class SalesDashboardController {
    @Autowired
    SalesDashboardsService salesDashboardsService;

    @GetMapping("/")
    @Operation(description = "Returns the sales dashboard")
    @ApiResponse(responseCode = "200", description = "Sales dashboard retrieved successfully")
    public SalesDashboardDto getDashboard() {
        return salesDashboardsService.getDashboard();
    }

    @GetMapping("/client-report")
    @Operation(description = "Returns the client report")
    @ApiResponse(responseCode = "200", description = "Client report retrieved successfully")
    public SalesClientReportDto getClientReport(@RequestParam UUID clientId) {
        return salesDashboardsService.getClientReport(clientId);
    }
}
