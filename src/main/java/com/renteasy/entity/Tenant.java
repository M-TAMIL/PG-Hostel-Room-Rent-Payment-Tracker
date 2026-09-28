package com.renteasy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @NotNull(message = "Move-in date is required")
    @PastOrPresent(message = "Move-in date cannot be in the future")
    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    @Column(name = "monthly_rent", nullable = false)
    private Double monthlyRent;

    @Column(name = "security_deposit")
    private Double securityDeposit = 0.0;

    @NotNull(message = "Tenant status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = true)
    @JsonIgnoreProperties({"tenants"})
    private Room room;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"tenant"})
    private List<RentPayment> payments = new ArrayList<>();

    public Tenant() {
    }

    public Tenant(String fullName, String phone, String email, LocalDate moveInDate, Double monthlyRent, Double securityDeposit, Room room) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.moveInDate = moveInDate;
        this.monthlyRent = monthlyRent;
        this.securityDeposit = securityDeposit != null ? securityDeposit : 0.0;
        this.status = TenantStatus.ACTIVE;
        this.room = room;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<RentPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<RentPayment> payments) {
        this.payments = payments;
    }
}
