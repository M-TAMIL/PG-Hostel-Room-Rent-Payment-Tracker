package com.renteasy.service;

import com.renteasy.dto.DashboardStatsResponse;
import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;
import com.renteasy.repository.RentPaymentRepository;
import com.renteasy.repository.RoomRepository;
import com.renteasy.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final RentPaymentRepository rentPaymentRepository;
    private final TenantService tenantService;

    @Autowired
    public DashboardService(RoomRepository roomRepository,
                            TenantRepository tenantRepository,
                            RentPaymentRepository rentPaymentRepository,
                            TenantService tenantService) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.rentPaymentRepository = rentPaymentRepository;
        this.tenantService = tenantService;
    }

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setTotalRooms(roomRepository.count());
        stats.setOccupiedRooms(roomRepository.countByIsOccupied(true));
        stats.setVacantRooms(roomRepository.countByIsOccupied(false));

        stats.setTotalTenants(tenantRepository.count());
        stats.setActiveTenants(tenantRepository.countByStatus(TenantStatus.ACTIVE));
        stats.setVacatedTenants(tenantRepository.countByStatus(TenantStatus.VACATED));

        String currentMonth = YearMonth.now().toString();
        stats.setCurrentMonth(currentMonth);

        Double currentMonthSum = rentPaymentRepository.sumAmountByPaymentMonth(currentMonth);
        stats.setCurrentMonthCollection(currentMonthSum != null ? currentMonthSum : 0.0);

        List<Tenant> unpaidTenants = tenantRepository.findActiveTenantsUnpaidForMonth(currentMonth);
        stats.setCurrentMonthUnpaidTenants(unpaidTenants.size());

        Double lifetimeSum = rentPaymentRepository.sumTotalAmount();
        stats.setTotalLifetimeCollection(lifetimeSum != null ? lifetimeSum : 0.0);

        // Calculate total pending dues across all active tenants
        List<Tenant> activeTenants = tenantRepository.findByStatus(TenantStatus.ACTIVE);
        double totalPending = activeTenants.stream()
                .mapToDouble(t -> tenantService.buildDuesResponse(t).getTotalPendingDues())
                .sum();
        stats.setTotalPendingDues(totalPending);

        return stats;
    }
}
