package com.savbill.partnermanagement.modules.partnerdocDetails.repository;



import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerDebitDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerCreditDocRepository extends JpaRepository<PartnerDebitDocument, Integer>, QuerydslPredicateExecutor<PartnerDebitDocument> {

    List<PartnerDebitDocument> findByBillrunid(Integer billRunId);

    @Query("select t from PartnerCreditDocument t where t.isDelete=false")
    List<PartnerDebitDocument> findAll();

    @Query("update PartnerCreditDocument b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<PartnerDebitDocument> getAllByPartner(Partner partner);

}
