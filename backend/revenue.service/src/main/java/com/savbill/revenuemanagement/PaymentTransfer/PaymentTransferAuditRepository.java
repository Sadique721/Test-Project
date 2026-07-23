package com.savbill.revenuemanagement.PaymentTransfer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransferAuditRepository extends JpaRepository<PaymentTransferAudit , Long> {

    @Query(value = "select * from tblttransfer_audit where main_cust_id =:custId",nativeQuery = true)
    Page<PaymentTransferAudit> getPaymentAuditByCustomerAudit(@Param("custId")Integer custId, Pageable pageable);

}
