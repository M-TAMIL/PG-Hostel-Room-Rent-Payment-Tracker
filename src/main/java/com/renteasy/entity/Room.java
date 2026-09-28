package com.renteasy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", unique = true, nullable = false, length = 50)
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType = RoomType.SINGLE;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity = 1;

    @Min(value = 0, message = "Current occupancy cannot be negative")
    @Column(name = "current_occupancy", nullable = false)
    private Integer currentOccupancy = 0;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    @Column(name = "monthly_rent", nullable = false)
    private Double monthlyRent;

    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied = false;

    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tenant> tenants = new ArrayList<>();

    public Room() {
    }

    public Room(String roomNumber, RoomType roomType, Integer capacity, Double monthlyRent) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity != null ? capacity : 1;
        this.monthlyRent = monthlyRent;
        this.currentOccupancy = 0;
        this.isOccupied = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        updateOccupiedStatus();
    }

    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
        updateOccupiedStatus();
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public void updateOccupiedStatus() {
        this.isOccupied = (this.currentOccupancy != null && this.capacity != null && this.currentOccupancy >= this.capacity);
    }

    public void incrementOccupancy() {
        this.currentOccupancy = (this.currentOccupancy == null ? 0 : this.currentOccupancy) + 1;
        updateOccupiedStatus();
    }

    public void decrementOccupancy() {
        this.currentOccupancy = (this.currentOccupancy == null || this.currentOccupancy <= 0) ? 0 : this.currentOccupancy - 1;
        updateOccupiedStatus();
    }
}
