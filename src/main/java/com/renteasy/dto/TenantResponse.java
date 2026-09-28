package com.renteasy.dto;

import com.renteasy.entity.RoomType;
import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;

import java.time.LocalDate;

public class TenantResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private Double monthlyRent;
    private Double securityDeposit;
    private TenantStatus status;
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;

    public TenantResponse() {
    }

    public static TenantResponse fromEntity(Tenant tenant) {
        TenantResponse response = new TenantResponse();
        response.setId(tenant.getId());
        response.setFullName(tenant.getFullName());
        response.setPhone(tenant.getPhone());
        response.setEmail(tenant.getEmail());
        response.setMoveInDate(tenant.getMoveInDate());
        response.setMoveOutDate(tenant.getMoveOutDate());
        response.setMonthlyRent(tenant.getMonthlyRent());
        response.setSecurityDeposit(tenant.getSecurityDeposit());
        response.setStatus(tenant.getStatus());
        if (tenant.getRoom() != null) {
            response.setRoomId(tenant.getRoom().getId());
            response.setRoomNumber(tenant.getRoom().getRoomNumber());
            response.setRoomType(tenant.getRoom().getRoomType());
        }
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}
