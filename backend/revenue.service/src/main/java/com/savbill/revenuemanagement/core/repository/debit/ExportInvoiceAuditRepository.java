package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.ExportInvoiceAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface ExportInvoiceAuditRepository extends JpaRepository<ExportInvoiceAudit, Integer>, JpaSpecificationExecutor<ExportInvoiceAudit> {

    ExportInvoiceAudit findByRequestId(String requestId);

    @Query(value = "select * from tbltexportinvoiceaudit t where thread_name = :threadName", nativeQuery = true)
    Page<ExportInvoiceAudit> findAll(Pageable pageable, @Param("threadName")String threadName);

    @Query(value = "select * from tbltexportinvoiceaudit t where mvno_id in :mvnoIds and thread_name = :threadName", nativeQuery = true)
    Page<ExportInvoiceAudit> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds, @Param("threadName")String threadName);

}
