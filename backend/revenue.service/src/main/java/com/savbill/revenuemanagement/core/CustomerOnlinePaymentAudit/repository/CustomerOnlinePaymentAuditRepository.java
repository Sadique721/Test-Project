package com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.repository;

import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.domain.CustomerOnlinePaymentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerOnlinePaymentAuditRepository extends JpaRepository<CustomerOnlinePaymentAudit,Long> {
    @Query(value = "SELECT MAX(id) AS latestId FROM tbltonlinepaymentaudit t", nativeQuery = true)
    Long getLatestId();

    CustomerOnlinePaymentAudit findByOrderId(Long orderId);
}
