package com.renteasy.service;

import com.renteasy.dto.TenantDuesResponse;
import com.renteasy.dto.TenantRegistrationRequest;
import com.renteasy.dto.TenantResponse;
import com.renteasy.entity.RentPayment;
import com.renteasy.entity.Room;
import com.renteasy.entity.RoomType;
import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;
import com.renteasy.exception.RoomAlreadyOccupiedException;
import com.renteasy.repository.RentPaymentRepository;
import com.renteasy.repository.RoomRepository;
import com.renteasy.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RentPaymentRepository rentPaymentRepository;

    @InjectMocks
    private TenantService tenantService;

    private Room availableRoom;
    private Room occupiedRoom;

    @BeforeEach
    void setUp() {
        availableRoom = new Room("R101", RoomType.SINGLE, 1, 8000.0);
        availableRoom.setId(1L);
        availableRoom.setCurrentOccupancy(0);
        availableRoom.setIsOccupied(false);

        occupiedRoom = new Room("R102", RoomType.SINGLE, 1, 8500.0);
        occupiedRoom.setId(2L);
        occupiedRoom.setCurrentOccupancy(1);
        occupiedRoom.setIsOccupied(true);
    }

    @Test
    @DisplayName("Business Rule 1: A room cannot be assigned to a new tenant while it is occupied")
    void testRegisterTenant_WhenRoomOccupied_ShouldThrowException() {
        when(roomRepository.findById(2L)).thenReturn(Optional.of(occupiedRoom));

        TenantRegistrationRequest request = new TenantRegistrationRequest(
                "Jane Doe", "9876543210", "jane@example.com",
                LocalDate.now(), 2L, 8500.0, 10000.0
        );

        RoomAlreadyOccupiedException exception = assertThrows(
                RoomAlreadyOccupiedException.class,
                () -> tenantService.registerTenant(request)
        );

        assertTrue(exception.getMessage().contains("already occupied"));
        verify(tenantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Normal Path: Register tenant successfully when room is available")
    void testRegisterTenant_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(availableRoom));
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            t.setId(10L);
            return t;
        });

        TenantRegistrationRequest request = new TenantRegistrationRequest(
                "John Doe", "9876543210", "john@example.com",
                LocalDate.now(), 1L, 8000.0, 16000.0
        );

        TenantResponse response = tenantService.registerTenant(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("R101", response.getRoomNumber());
        assertEquals(1, availableRoom.getCurrentOccupancy());
        assertTrue(availableRoom.getIsOccupied());
        verify(roomRepository, times(1)).save(availableRoom);
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Business Rule 2: Pending dues calculated as months since move in minus months paid")
    void testCalculatePendingDues_FormulaEnforced() {
        YearMonth currentYm = YearMonth.now();
        YearMonth twoMonthsAgo = currentYm.minusMonths(2);

        Tenant tenant = new Tenant(
                "Aarav", "9999988888", "aarav@test.com",
                twoMonthsAgo.atDay(1), 8000.0, 16000.0, availableRoom
        );
        tenant.setId(5L);

        when(tenantRepository.findById(5L)).thenReturn(Optional.of(tenant));

        // Aarav paid for 1 month only (2 months ago)
        RentPayment p1 = new RentPayment();
        p1.setPaymentMonth(twoMonthsAgo.toString());
        p1.setAmount(8000.0);

        when(rentPaymentRepository.findByTenantIdOrderByPaymentMonthDesc(5L))
                .thenReturn(List.of(p1));

        TenantDuesResponse dues = tenantService.calculatePendingDues(5L);

        assertEquals(3, dues.getTotalMonthsElapsed()); // 2 months ago, 1 month ago, current month = 3
        assertEquals(1, dues.getMonthsPaidCount());    // 1 paid
        assertEquals(2, dues.getPendingMonthsCount());  // 3 - 1 = 2 pending
        assertEquals(16000.0, dues.getTotalPendingDues()); // 2 * 8000 = 16000
        assertFalse(dues.isCurrentMonthPaid());
    }

    @Test
    @DisplayName("Feature 5: Vacate tenant sets status to VACATED and frees the room")
    void testVacateTenant_FreesRoom() {
        Room occupied = new Room("R103", RoomType.SINGLE, 1, 9000.0);
        occupied.setCurrentOccupancy(1);
        occupied.setIsOccupied(true);

        Tenant tenant = new Tenant(
                "Suresh", "9898989898", "suresh@test.com",
                LocalDate.now().minusMonths(3), 9000.0, 18000.0, occupied
        );
        tenant.setId(7L);

        when(tenantRepository.findById(7L)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(i -> i.getArgument(0));

        TenantResponse response = tenantService.vacateTenant(7L);

        assertEquals(TenantStatus.VACATED, response.getStatus());
        assertEquals(0, occupied.getCurrentOccupancy());
        assertFalse(occupied.getIsOccupied());
        verify(roomRepository, times(1)).save(occupied);
    }
}
