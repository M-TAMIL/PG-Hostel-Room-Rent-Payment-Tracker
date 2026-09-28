package com.renteasy.controller;

import com.renteasy.dto.ApiResponse;
import com.renteasy.dto.TenantDuesResponse;
import com.renteasy.dto.TenantRegistrationRequest;
import com.renteasy.dto.TenantResponse;
import com.renteasy.entity.TenantStatus;
import com.renteasy.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {

    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Core Feature 1: Register a tenant and assign them to a room with monthly rent.
     * Enforces Rule 1: A room cannot be assigned to a new tenant while it is occupied.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> registerTenant(@Valid @RequestBody TenantRegistrationRequest request) {
        TenantResponse response = tenantService.registerTenant(request);
        return new ResponseEntity<>(
                ApiResponse.success("Tenant successfully registered and assigned to room " + response.getRoomNumber(), response),
                HttpStatus.CREATED
        );
    }

    /**
     * Core Feature 3: View pending dues per tenant.
     * Enforces Rule 2: Pending dues are calculated as months since move in minus months paid.
     */
    @GetMapping("/{tenantId}/dues")
    public ResponseEntity<ApiResponse<TenantDuesResponse>> getPendingDues(@PathVariable Long tenantId) {
        TenantDuesResponse dues = tenantService.calculatePendingDues(tenantId);
        return ResponseEntity.ok(
                ApiResponse.success("Pending dues calculated successfully for tenant ID " + tenantId, dues)
        );
    }

    /**
     * Core Feature 4: List tenants who have not paid for the current month.
     */
    @GetMapping("/unpaid-current-month")
    public ResponseEntity<ApiResponse<List<TenantDuesResponse>>> getUnpaidTenantsForCurrentMonth() {
        List<TenantDuesResponse> unpaidTenants = tenantService.getUnpaidTenantsForCurrentMonth();
        return ResponseEntity.ok(
                ApiResponse.success("Found " + unpaidTenants.size() + " active tenant(s) with unpaid rent for current month", unpaidTenants)
        );
    }

    /**
     * Core Feature 5: Vacate a tenant and free the room.
     */
    @PutMapping("/{tenantId}/vacate")
    public ResponseEntity<ApiResponse<TenantResponse>> vacateTenant(@PathVariable Long tenantId) {
        TenantResponse response = tenantService.vacateTenant(tenantId);
        return ResponseEntity.ok(
                ApiResponse.success("Tenant " + response.getFullName() + " vacated successfully. Room " + response.getRoomNumber() + " is now free.", response)
        );
    }

    /**
     * List all tenants with optional search and status filter.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponse>>> getAllTenants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TenantStatus status) {
        List<TenantResponse> tenants = tenantService.getAllTenants(keyword, status);
        return ResponseEntity.ok(
                ApiResponse.success("Tenants retrieved successfully", tenants)
        );
    }

    /**
     * Get single tenant details by ID.
     */
    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(@PathVariable Long tenantId) {
        TenantResponse tenant = tenantService.getTenantById(tenantId);
        return ResponseEntity.ok(
                ApiResponse.success("Tenant found", tenant)
        );
    }
}
