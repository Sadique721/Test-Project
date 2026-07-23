package com.savbill.revenuemanagement.core.repository.partner;


import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PartnerDebitDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface PartnerDebitDocRepository extends JpaRepository<PartnerDebitDocument, Integer>{

    List<PartnerDebitDocument> findByBillrunid(Integer billRunId);

    @Query("select t from PartnerDebitDocument t where t.isDelete=false")
    List<PartnerDebitDocument> findAll();

    @Query("update PartnerDebitDocument b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<PartnerDebitDocument> getAllByPartner(Partner partner);

    @Query("select t from PartnerDebitDocument t where t.isDelete=false and id=:invoiceId")
    PartnerDebitDocument findAllInDebitDocId(@Param("invoiceId") Integer invoiceId);
}
