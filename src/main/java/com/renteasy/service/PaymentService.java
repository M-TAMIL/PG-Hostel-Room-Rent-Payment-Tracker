package com.renteasy.service;

import com.renteasy.dto.PaymentRequest;
import com.renteasy.dto.PaymentResponse;
import com.renteasy.entity.RentPayment;
import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;
import com.renteasy.exception.DuplicatePaymentException;
import com.renteasy.exception.InvalidOperationException;
import com.renteasy.exception.ResourceNotFoundException;
import com.renteasy.repository.RentPaymentRepository;
import com.renteasy.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public PaymentService(RentPaymentRepository rentPaymentRepository, TenantRepository tenantRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
        this.tenantRepository = tenantRepository;
    }

    /**
     * Core Feature 2: Log a rent payment for a specific month.
     * Enforces rules:
     * - Tenant must exist.
     * - Cannot log duplicate payment for the same month.
     * - Payment month cannot be before tenant move-in date.
     */
    public PaymentResponse logPayment(PaymentRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + request.getTenantId()));

        YearMonth targetMonth = YearMonth.parse(request.getPaymentMonth());
        YearMonth moveInMonth = YearMonth.from(tenant.getMoveInDate());

        // Validate payment month cannot be before move-in date
        if (targetMonth.isBefore(moveInMonth)) {
            throw new InvalidOperationException(
                    "Payment month (" + request.getPaymentMonth() + ") cannot precede tenant's move-in month (" + moveInMonth + ")."
            );
        }

        // If vacated, cannot pay for months after move out
        if (tenant.getStatus() == TenantStatus.VACATED && tenant.getMoveOutDate() != null) {
            YearMonth moveOutMonth = YearMonth.from(tenant.getMoveOutDate());
            if (targetMonth.isAfter(moveOutMonth)) {
                throw new InvalidOperationException(
                        "Tenant was vacated in " + moveOutMonth + ". Cannot log payment for future month " + targetMonth + "."
                );
            }
        }

        // Business Rule: Check for duplicate payment for the same month
        if (rentPaymentRepository.existsByTenantIdAndPaymentMonth(tenant.getId(), request.getPaymentMonth())) {
            throw new DuplicatePaymentException(
                    "Rent payment for tenant '" + tenant.getFullName() + "' for month " + request.getPaymentMonth() + " has already been logged."
            );
        }

        // Generate clean unique receipt number
        String receiptNumber = generateReceiptNumber(request.getPaymentDate());

        RentPayment payment = new RentPayment(
                tenant,
                request.getAmount(),
                request.getPaymentMonth(),
                request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now(),
                request.getPaymentMode(),
                receiptNumber,
                request.getTransactionReference(),
                request.getNotes()
        );

        RentPayment saved = rentPaymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByTenantId(Long tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResourceNotFoundException("Tenant not found with ID: " + tenantId);
        }
        return rentPaymentRepository.findByTenantIdOrderByPaymentMonthDesc(tenantId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments(String paymentMonth) {
        List<RentPayment> payments;
        if (paymentMonth != null && !paymentMonth.trim().isEmpty()) {
            payments = rentPaymentRepository.findByPaymentMonth(paymentMonth.trim());
        } else {
            payments = rentPaymentRepository.findAllByOrderByPaymentDateDesc();
        }
        return payments.stream().map(PaymentResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        RentPayment payment = rentPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return PaymentResponse.fromEntity(payment);
    }

    private String generateReceiptNumber(LocalDate date) {
        String datePart = (date != null ? date : LocalDate.now()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "REC-" + datePart + "-" + randomPart;
    }
}
