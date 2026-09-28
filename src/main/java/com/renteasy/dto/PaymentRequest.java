package com.renteasy.dto;

import com.renteasy.entity.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class PaymentRequest {

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Payment month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Payment month must be in YYYY-MM format (e.g. 2026-03)")
    private String paymentMonth;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment mode is required (CASH, UPI, BANK_TRANSFER, CHEQUE)")
    private PaymentMode paymentMode;

    private String transactionReference;
    private String notes;

    public PaymentRequest() {
    }

    public PaymentRequest(Long tenantId, Double amount, String paymentMonth, LocalDate paymentDate, PaymentMode paymentMode, String transactionReference, String notes) {
        this.tenantId = tenantId;
        this.amount = amount;
        this.paymentMonth = paymentMonth;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.transactionReference = transactionReference;
        this.notes = notes;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(String paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
