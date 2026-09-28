package com.renteasy.repository;

import com.renteasy.entity.RentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    List<RentPayment> findByTenantIdOrderByPaymentMonthDesc(Long tenantId);

    List<RentPayment> findByTenantId(Long tenantId);

    boolean existsByTenantIdAndPaymentMonth(Long tenantId, String paymentMonth);

    Optional<RentPayment> findByTenantIdAndPaymentMonth(Long tenantId, String paymentMonth);

    List<RentPayment> findByPaymentMonth(String paymentMonth);

    List<RentPayment> findAllByOrderByPaymentDateDesc();

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM RentPayment p WHERE p.paymentMonth = :paymentMonth")
    Double sumAmountByPaymentMonth(@Param("paymentMonth") String paymentMonth);

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM RentPayment p")
    Double sumTotalAmount();
}
