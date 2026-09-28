package com.renteasy.service;

import com.renteasy.dto.TenantDuesResponse;
import com.renteasy.dto.TenantRegistrationRequest;
import com.renteasy.dto.TenantResponse;
import com.renteasy.entity.RentPayment;
import com.renteasy.entity.Room;
import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;
import com.renteasy.exception.InvalidOperationException;
import com.renteasy.exception.ResourceNotFoundException;
import com.renteasy.exception.RoomAlreadyOccupiedException;
import com.renteasy.repository.RentPaymentRepository;
import com.renteasy.repository.RoomRepository;
import com.renteasy.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final RentPaymentRepository rentPaymentRepository;

    @Autowired
    public TenantService(TenantRepository tenantRepository,
                         RoomRepository roomRepository,
                         RentPaymentRepository rentPaymentRepository) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
        this.rentPaymentRepository = rentPaymentRepository;
    }

    /**
     * Core Feature 1: Register a tenant and assign them to a room with monthly rent.
     * Business Rule 1 Enforced: A room cannot be assigned to a new tenant while it is occupied.
     */
    public TenantResponse registerTenant(TenantRegistrationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

        // Business Rule 1: Reject assignment if room is occupied or at maximum capacity
        if (Boolean.TRUE.equals(room.getIsOccupied()) || room.getCurrentOccupancy() >= room.getCapacity()) {
            throw new RoomAlreadyOccupiedException(
                    "Room " + room.getRoomNumber() + " is already occupied (Capacity: " +
                    room.getCapacity() + ", Occupied: " + room.getCurrentOccupancy() + "). Cannot assign new tenant."
            );
        }

        // Monthly rent: either custom rent for tenant or standard room rent
        Double rent = (request.getMonthlyRent() != null && request.getMonthlyRent() > 0)
                ? request.getMonthlyRent()
                : room.getMonthlyRent();

        Tenant tenant = new Tenant(
                request.getFullName().trim(),
                request.getPhone().trim(),
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getMoveInDate(),
                rent,
                request.getSecurityDeposit(),
                room
        );

        // Update room occupancy
        room.incrementOccupancy();
        roomRepository.save(room);

        Tenant savedTenant = tenantRepository.save(tenant);
        return TenantResponse.fromEntity(savedTenant);
    }

    /**
     * Core Feature 3: View pending dues per tenant.
     * Business Rule 2 Enforced: Pending dues are calculated as months since move in minus months paid.
     */
    @Transactional(readOnly = true)
    public TenantDuesResponse calculatePendingDues(Long tenantId) {
        Tenant tenant = findTenantEntityById(tenantId);
        return buildDuesResponse(tenant);
    }

    /**
     * Core Feature 4: List tenants who have not paid for the current month.
     */
    @Transactional(readOnly = true)
    public List<TenantDuesResponse> getUnpaidTenantsForCurrentMonth() {
        String currentMonth = YearMonth.now().toString(); // e.g. "2026-03"
        List<Tenant> unpaidTenants = tenantRepository.findActiveTenantsUnpaidForMonth(currentMonth);

        return unpaidTenants.stream()
                .map(this::buildDuesResponse)
                .collect(Collectors.toList());
    }

    /**
     * Core Feature 5: Vacate a tenant and free the room.
     */
    public TenantResponse vacateTenant(Long tenantId) {
        Tenant tenant = findTenantEntityById(tenantId);

        if (tenant.getStatus() == TenantStatus.VACATED) {
            throw new InvalidOperationException("Tenant " + tenant.getFullName() + " is already marked as VACATED.");
        }

        tenant.setStatus(TenantStatus.VACATED);
        tenant.setMoveOutDate(LocalDate.now());

        // Free the room slot
        Room room = tenant.getRoom();
        if (room != null) {
            room.decrementOccupancy();
            roomRepository.save(room);
        }

        Tenant updatedTenant = tenantRepository.save(tenant);
        return TenantResponse.fromEntity(updatedTenant);
    }

    /**
     * Helper to compute dues according to Business Rule 2:
     * pending dues = months since move in minus months paid
     */
    public TenantDuesResponse buildDuesResponse(Tenant tenant) {
        TenantDuesResponse dues = new TenantDuesResponse();
        dues.setTenantId(tenant.getId());
        dues.setTenantName(tenant.getFullName());
        dues.setPhone(tenant.getPhone());
        dues.setRoomNumber(tenant.getRoom() != null ? tenant.getRoom().getRoomNumber() : "N/A");
        dues.setMoveInDate(tenant.getMoveInDate());
        dues.setMoveOutDate(tenant.getMoveOutDate());
        dues.setStatus(tenant.getStatus());
        dues.setMonthlyRent(tenant.getMonthlyRent());

        // Calculate all months from moveInDate to current month (or moveOutDate if vacated)
        YearMonth startMonth = YearMonth.from(tenant.getMoveInDate());
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth endMonth = (tenant.getStatus() == TenantStatus.VACATED && tenant.getMoveOutDate() != null)
                ? YearMonth.from(tenant.getMoveOutDate())
                : currentYearMonth;

        // If move-in is in future month (edge case safeguard)
        if (startMonth.isAfter(endMonth)) {
            endMonth = startMonth;
        }

        List<String> allEligibleMonths = new ArrayList<>();
        YearMonth cursor = startMonth;
        while (!cursor.isAfter(endMonth)) {
            allEligibleMonths.add(cursor.toString());
            cursor = cursor.plusMonths(1);
        }

        // Get all payments made by this tenant
        List<RentPayment> payments = rentPaymentRepository.findByTenantIdOrderByPaymentMonthDesc(tenant.getId());
        Set<String> paidMonthsSet = payments.stream()
                .map(RentPayment::getPaymentMonth)
                .collect(Collectors.toSet());

        List<String> paidMonthsList = allEligibleMonths.stream()
                .filter(paidMonthsSet::contains)
                .collect(Collectors.toList());

        List<String> unpaidMonthsList = allEligibleMonths.stream()
                .filter(m -> !paidMonthsSet.contains(m))
                .collect(Collectors.toList());

        long totalMonthsElapsed = allEligibleMonths.size();
        long monthsPaidCount = paidMonthsList.size();
        long pendingMonthsCount = Math.max(0, totalMonthsElapsed - monthsPaidCount);
        double totalPaidAmount = payments.stream().mapToDouble(RentPayment::getAmount).sum();
        double totalPendingDues = pendingMonthsCount * tenant.getMonthlyRent();

        dues.setTotalMonthsElapsed(totalMonthsElapsed);
        dues.setMonthsPaidCount(monthsPaidCount);
        dues.setPendingMonthsCount(pendingMonthsCount);
        dues.setTotalPaidAmount(totalPaidAmount);
        dues.setTotalPendingDues(totalPendingDues);
        dues.setPaidMonths(paidMonthsList);
        dues.setUnpaidMonths(unpaidMonthsList);
        dues.setCurrentMonthPaid(paidMonthsSet.contains(currentYearMonth.toString()));

        return dues;
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants(String keyword, TenantStatus status) {
        List<Tenant> tenants;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tenants = tenantRepository.searchTenants(keyword.trim(), status);
        } else if (status != null) {
            tenants = tenantRepository.findByStatus(status);
        } else {
            tenants = tenantRepository.findAll();
        }
        return tenants.stream().map(TenantResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TenantResponse getTenantById(Long id) {
        Tenant tenant = findTenantEntityById(id);
        return TenantResponse.fromEntity(tenant);
    }

    public Tenant findTenantEntityById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
    }
}
