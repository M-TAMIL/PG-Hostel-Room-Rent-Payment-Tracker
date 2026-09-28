package com.renteasy.dto;

public class DashboardStatsResponse {
    private long totalRooms;
    private long occupiedRooms;
    private long vacantRooms;
    private long totalTenants;
    private long activeTenants;
    private long vacatedTenants;
    private String currentMonth;
    private Double currentMonthCollection;
    private long currentMonthUnpaidTenants;
    private Double totalPendingDues;
    private Double totalLifetimeCollection;

    public DashboardStatsResponse() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getVacantRooms() {
        return vacantRooms;
    }

    public void setVacantRooms(long vacantRooms) {
        this.vacantRooms = vacantRooms;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getActiveTenants() {
        return activeTenants;
    }

    public void setActiveTenants(long activeTenants) {
        this.activeTenants = activeTenants;
    }

    public long getVacatedTenants() {
        return vacatedTenants;
    }

    public void setVacatedTenants(long vacatedTenants) {
        this.vacatedTenants = vacatedTenants;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public Double getCurrentMonthCollection() {
        return currentMonthCollection;
    }

    public void setCurrentMonthCollection(Double currentMonthCollection) {
        this.currentMonthCollection = currentMonthCollection;
    }

    public long getCurrentMonthUnpaidTenants() {
        return currentMonthUnpaidTenants;
    }

    public void setCurrentMonthUnpaidTenants(long currentMonthUnpaidTenants) {
        this.currentMonthUnpaidTenants = currentMonthUnpaidTenants;
    }

    public Double getTotalPendingDues() {
        return totalPendingDues;
    }

    public void setTotalPendingDues(Double totalPendingDues) {
        this.totalPendingDues = totalPendingDues;
    }

    public Double getTotalLifetimeCollection() {
        return totalLifetimeCollection;
    }

    public void setTotalLifetimeCollection(Double totalLifetimeCollection) {
        this.totalLifetimeCollection = totalLifetimeCollection;
    }
}
