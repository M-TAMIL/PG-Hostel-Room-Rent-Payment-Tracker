package com.renteasy.repository;

import com.renteasy.entity.Tenant;
import com.renteasy.entity.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    List<Tenant> findByStatus(TenantStatus status);

    List<Tenant> findByRoomId(Long roomId);

    List<Tenant> findByRoomIdAndStatus(Long roomId, TenantStatus status);

    long countByStatus(TenantStatus status);

    boolean existsByPhone(String phone);

    @Query("SELECT t FROM Tenant t WHERE t.status = com.renteasy.entity.TenantStatus.ACTIVE " +
           "AND t.id NOT IN (SELECT p.tenant.id FROM RentPayment p WHERE p.paymentMonth = :paymentMonth)")
    List<Tenant> findActiveTenantsUnpaidForMonth(@Param("paymentMonth") String paymentMonth);

    @Query("SELECT t FROM Tenant t WHERE " +
           "(:keyword IS NULL OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR t.phone LIKE CONCAT('%', :keyword, '%')) " +
           "AND (:status IS NULL OR t.status = :status)")
    List<Tenant> searchTenants(@Param("keyword") String keyword, @Param("status") TenantStatus status);
}
