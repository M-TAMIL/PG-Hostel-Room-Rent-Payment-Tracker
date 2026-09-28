package com.renteasy.config;

import com.renteasy.dto.PaymentRequest;
import com.renteasy.dto.RoomRequest;
import com.renteasy.dto.TenantRegistrationRequest;
import com.renteasy.dto.TenantResponse;
import com.renteasy.entity.PaymentMode;
import com.renteasy.entity.RoomType;
import com.renteasy.repository.RoomRepository;
import com.renteasy.service.PaymentService;
import com.renteasy.service.RoomService;
import com.renteasy.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final TenantService tenantService;
    private final PaymentService paymentService;

    public DataInitializer(RoomRepository roomRepository,
                           RoomService roomService,
                           TenantService tenantService,
                           PaymentService paymentService) {
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.tenantService = tenantService;
        this.paymentService = paymentService;
    }

    @Override
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Seeding initial PG/Hostel rooms, tenants, and payments...");

        // 1. Create Sample Rooms
        var r101 = roomService.createRoom(new RoomRequest("R101", RoomType.SINGLE, 1, 8000.0));
        var r102 = roomService.createRoom(new RoomRequest("R102", RoomType.SINGLE, 1, 8500.0));
        var r103 = roomService.createRoom(new RoomRequest("R103", RoomType.DOUBLE, 2, 6000.0));
        var r104 = roomService.createRoom(new RoomRequest("R104", RoomType.DOUBLE, 2, 6500.0));
        var r201 = roomService.createRoom(new RoomRequest("R201", RoomType.DELUXE, 1, 12000.0));
        var r202 = roomService.createRoom(new RoomRequest("R202", RoomType.DELUXE, 1, 12000.0));

        // 2. Register Sample Tenants
        YearMonth currentYm = YearMonth.now();
        YearMonth oneMonthAgo = currentYm.minusMonths(1);
        YearMonth twoMonthsAgo = currentYm.minusMonths(2);

        // Tenant 1: Aarav Patel (In R101, single room -> now R101 is occupied)
        TenantResponse t1 = tenantService.registerTenant(new TenantRegistrationRequest(
                "Aarav Patel",
                "9876543210",
                "aarav.patel@example.com",
                twoMonthsAgo.atDay(1),
                r101.getId(),
                8000.0,
                16000.0
        ));

        // Tenant 2: Rohan Verma (In R103, double room -> 1 bed occupied, 1 bed free)
        TenantResponse t2 = tenantService.registerTenant(new TenantRegistrationRequest(
                "Rohan Verma",
                "9123456780",
                "rohan.verma@example.com",
                oneMonthAgo.atDay(5),
                r103.getId(),
                6000.0,
                12000.0
        ));

        // Tenant 3: Sneha Reddy (In R201, deluxe room -> R201 occupied)
        TenantResponse t3 = tenantService.registerTenant(new TenantRegistrationRequest(
                "Sneha Reddy",
                "9988776655",
                "sneha.reddy@example.com",
                oneMonthAgo.atDay(10),
                r201.getId(),
                12000.0,
                24000.0
        ));

        // 3. Log Payments to demonstrate Dues & Unpaid logic:
        // Aarav paid 2 months ago and 1 month ago, but has NOT paid for the current month!
        paymentService.logPayment(new PaymentRequest(
                t1.getId(),
                8000.0,
                twoMonthsAgo.toString(),
                twoMonthsAgo.atDay(3),
                PaymentMode.CASH,
                "CASH-M1-01",
                "First month rent paid in cash"
        ));
        paymentService.logPayment(new PaymentRequest(
                t1.getId(),
                8000.0,
                oneMonthAgo.toString(),
                oneMonthAgo.atDay(4),
                PaymentMode.UPI,
                "UPI-TXN-90219",
                "Second month rent via Google Pay"
        ));

        // Sneha paid for last month and also paid for current month!
        paymentService.logPayment(new PaymentRequest(
                t3.getId(),
                12000.0,
                oneMonthAgo.toString(),
                oneMonthAgo.atDay(11),
                PaymentMode.BANK_TRANSFER,
                "IMPS-8871029",
                "Rent transfer"
        ));
        paymentService.logPayment(new PaymentRequest(
                t3.getId(),
                12000.0,
                currentYm.toString(),
                currentYm.atDay(2),
                PaymentMode.UPI,
                "UPI-TXN-11002",
                "Current month rent paid in advance"
        ));

        // Rohan Verma has paid 0 months -> has 2 months pending dues!

        log.info("Initial data seeded successfully!");
        log.info("R101 is fully occupied by Aarav Patel (Unpaid for current month: {})", currentYm);
        log.info("R103 has 1 bed occupied by Rohan Verma (Pending dues: 2 months)");
        log.info("R201 is fully occupied by Sneha Reddy (All dues cleared!)");
        log.info("R102, R104, R202 are available for new tenants.");
    }
}
