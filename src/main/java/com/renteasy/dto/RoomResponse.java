package com.renteasy.dto;

import com.renteasy.entity.Room;
import com.renteasy.entity.RoomType;

public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomType roomType;
    private Integer capacity;
    private Integer currentOccupancy;
    private Double monthlyRent;
    private Boolean isOccupied;
    private Integer availableBeds;

    public RoomResponse() {
    }

    public static RoomResponse fromEntity(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomType(room.getRoomType());
        response.setCapacity(room.getCapacity());
        response.setCurrentOccupancy(room.getCurrentOccupancy());
        response.setMonthlyRent(room.getMonthlyRent());
        response.setIsOccupied(room.getIsOccupied());
        int available = (room.getCapacity() != null ? room.getCapacity() : 1) - (room.getCurrentOccupancy() != null ? room.getCurrentOccupancy() : 0);
        response.setAvailableBeds(Math.max(0, available));
        return response;
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
    }

    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
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

    public Integer getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(Integer availableBeds) {
        this.availableBeds = availableBeds;
    }
}
