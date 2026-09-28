package com.renteasy.dto;

import com.renteasy.entity.PaymentMode;
import com.renteasy.entity.RentPayment;

import java.time.LocalDate;

public class PaymentResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String phone;
    private String roomNumber;
    private Double amount;
    private String paymentMonth;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
    private String receiptNumber;
    private String transactionReference;
    private String notes;

    public PaymentResponse() {
    }

    public static PaymentResponse fromEntity(RentPayment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        if (payment.getTenant() != null) {
            response.setTenantId(payment.getTenant().getId());
            response.setTenantName(payment.getTenant().getFullName());
            response.setPhone(payment.getTenant().getPhone());
            if (payment.getTenant().getRoom() != null) {
                response.setRoomNumber(payment.getTenant().getRoom().getRoomNumber());
            }
        }
        response.setAmount(payment.getAmount());
        response.setPaymentMonth(payment.getPaymentMonth());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentMode(payment.getPaymentMode());
        response.setReceiptNumber(payment.getReceiptNumber());
        response.setTransactionReference(payment.getTransactionReference());
        response.setNotes(payment.getNotes());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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
