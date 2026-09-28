package com.renteasy.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class TenantRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Move-in date is required")
    @PastOrPresent(message = "Move-in date cannot be in the future")
    private LocalDate moveInDate;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @Positive(message = "Monthly rent must be positive")
    private Double monthlyRent; // Optional: if omitted, room's monthlyRent is used

    @Min(value = 0, message = "Security deposit cannot be negative")
    private Double securityDeposit = 0.0;

    public TenantRegistrationRequest() {
    }

    public TenantRegistrationRequest(String fullName, String phone, String email, LocalDate moveInDate, Long roomId, Double monthlyRent, Double securityDeposit) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.moveInDate = moveInDate;
        this.roomId = roomId;
        this.monthlyRent = monthlyRent;
        this.securityDeposit = securityDeposit != null ? securityDeposit : 0.0;
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

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
}
