package com.renteasy.controller;

import com.renteasy.dto.ApiResponse;
import com.renteasy.dto.PaymentRequest;
import com.renteasy.dto.PaymentResponse;
import com.renteasy.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Core Feature 2: Log a rent payment for a specific month.
     * Prevents duplicate payments for the same tenant and month.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> logPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.logPayment(request);
        return new ResponseEntity<>(
                ApiResponse.success("Rent payment of ₹" + response.getAmount() + " successfully recorded for " + response.getPaymentMonth(), response),
                HttpStatus.CREATED
        );
    }

    /**
     * List all logged payments with optional month filter.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) String month) {
        List<PaymentResponse> payments = paymentService.getAllPayments(month);
        return ResponseEntity.ok(
                ApiResponse.success("Payments fetched successfully", payments)
        );
    }

    /**
     * Get payment history for a specific tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByTenantId(@PathVariable Long tenantId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByTenantId(tenantId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment history for tenant " + tenantId, payments)
        );
    }

    /**
     * Get single payment receipt by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Payment details retrieved", payment)
        );
    }
}
