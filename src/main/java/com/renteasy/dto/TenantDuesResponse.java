package com.renteasy.dto;

import com.renteasy.entity.TenantStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TenantDuesResponse {
    private Long tenantId;
    private String tenantName;
    private String phone;
    private String roomNumber;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private TenantStatus status;
    private Double monthlyRent;
    private long totalMonthsElapsed;
    private long monthsPaidCount;
    private long pendingMonthsCount;
    private Double totalPaidAmount;
    private Double totalPendingDues;
    private List<String> paidMonths = new ArrayList<>();
    private List<String> unpaidMonths = new ArrayList<>();
    private boolean isCurrentMonthPaid;

    public TenantDuesResponse() {
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

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public long getTotalMonthsElapsed() {
        return totalMonthsElapsed;
    }

    public void setTotalMonthsElapsed(long totalMonthsElapsed) {
        this.totalMonthsElapsed = totalMonthsElapsed;
    }

    public long getMonthsPaidCount() {
        return monthsPaidCount;
    }

    public void setMonthsPaidCount(long monthsPaidCount) {
        this.monthsPaidCount = monthsPaidCount;
    }

    public long getPendingMonthsCount() {
        return pendingMonthsCount;
    }

    public void setPendingMonthsCount(long pendingMonthsCount) {
        this.pendingMonthsCount = pendingMonthsCount;
    }

    public Double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(Double totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public Double getTotalPendingDues() {
        return totalPendingDues;
    }

    public void setTotalPendingDues(Double totalPendingDues) {
        this.totalPendingDues = totalPendingDues;
    }

    public List<String> getPaidMonths() {
        return paidMonths;
    }

    public void setPaidMonths(List<String> paidMonths) {
        this.paidMonths = paidMonths;
    }

    public List<String> getUnpaidMonths() {
        return unpaidMonths;
    }

    public void setUnpaidMonths(List<String> unpaidMonths) {
        this.unpaidMonths = unpaidMonths;
    }

    public boolean isCurrentMonthPaid() {
        return isCurrentMonthPaid;
    }

    public void setCurrentMonthPaid(boolean currentMonthPaid) {
        isCurrentMonthPaid = currentMonthPaid;
    }
}
