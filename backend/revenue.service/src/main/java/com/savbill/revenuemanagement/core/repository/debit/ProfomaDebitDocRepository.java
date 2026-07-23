package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.ProformaDebitDocument;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface ProfomaDebitDocRepository extends JpaRepository<ProformaDebitDocument,Integer> {


    @Query(
            value = "SELECT t.plantype, t.validity " +
                    "FROM tblmpostpaidplan t " +
                    "WHERE t.POSTPAIDPLANID = (" +
                    "   SELECT r.planid " +
                    "   FROM tblcustpackagerel r " +
                    "   WHERE r.custpackageid = (" +
                    "       SELECT d.custpackrelid " +
                    "       FROM TBLTTRIALDEBITDOCUMENT d " +
                    "       WHERE d.trialdebitdocumentid = :TraildebitDocId))",
            nativeQuery = true)
    Object[] findPlanTypeByDebitDocumentId(@Param("TraildebitDocId") Integer TraildebitDocId);



    @Query(
            value = "SELECT t.plantype, t.validity " +
                    "FROM tblmpostpaidplan t " +
                    "WHERE t.POSTPAIDPLANID = (" +
                    "   SELECT r.planid " +
                    "   FROM tbltproformadebitdocumentdetail r " +
                    "   WHERE r.proformadebitdocaddrid = :proformaAddrId)",
            nativeQuery = true
    )
    Object[] findPlanTypeByProformaAddrId(@Param("proformaAddrId") Integer proformaAddrId);

}
