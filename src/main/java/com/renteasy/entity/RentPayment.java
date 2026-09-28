package com.renteasy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "rent_payments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "payment_month"})
})
public class RentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tenant is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnoreProperties({"payments"})
    private Tenant tenant;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    @Column(nullable = false)
    private Double amount;

    @NotBlank(message = "Payment month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Payment month must be in YYYY-MM format (e.g. 2026-03)")
    @Column(name = "payment_month", nullable = false, length = 7)
    private String paymentMonth;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull(message = "Payment mode is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Column(name = "receipt_number", unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "notes", length = 255)
    private String notes;

    public RentPayment() {
    }

    public RentPayment(Tenant tenant, Double amount, String paymentMonth, LocalDate paymentDate, PaymentMode paymentMode, String receiptNumber, String transactionReference, String notes) {
        this.tenant = tenant;
        this.amount = amount;
        this.paymentMonth = paymentMonth;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.receiptNumber = receiptNumber;
        this.transactionReference = transactionReference;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
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
