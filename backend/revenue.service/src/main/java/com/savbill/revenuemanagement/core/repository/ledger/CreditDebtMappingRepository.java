package com.savbill.revenuemanagement.core.repository.ledger;

import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CreditDebtMappingRepository extends JpaRepository<CreditDebitDocMapping, Integer>, QuerydslPredicateExecutor<CreditDebitDocMapping> {

    List<CreditDebitDocMapping> findBydebtDocId(Integer debtDocId);

    List<CreditDebitDocMapping> findBydebtDocIdAndCreditDocId(Integer debtDocId,Integer creditDocId);


    List<CreditDebitDocMapping> findByCreditDocId(Integer creditDocId);

    List<CreditDebitDocMapping> findAllBydebtDocIdIn(List<Integer> debtDocId);
    @Query("select map from CreditDebitDocMapping map where map.debtDocId =:debitDocumentForCustomer")
    List<CreditDebitDocMapping> findCreditDebitDocMappingsForDebitDocument(Integer debitDocumentForCustomer);

    @Query("select map from CreditDebitDocMapping map inner join CreditDocument cd " +
            "on map.creditDocId = cd.id where map.debtDocId =:debtDocId and cd.type =:type")
    List<CreditDebitDocMapping> findMappingBydebtDocId(Integer debtDocId, String type);

    List<CreditDebitDocMapping> findAllBydebtDocId(Integer invoiceId);

    List<CreditDebitDocMapping> findAllByCreditDocIdAndAdjustedAmountNotNull(Integer creditDocId);

    List<CreditDebitDocMapping> findAllByWithdrawId(Integer creditDocId);

    List<Integer> findAllCreditDocIdByCreditDocIdIn(List<Integer> creditDocId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbltcreditdebitmapping SET adjustedamount=:adjustedAmount WHERE creddebtmappingid=:id",nativeQuery = true)
    void updateAdjustmentAmount(@Param("id") Integer id, @Param("adjustedAmount") Double adjustedAmount);

    @Query("SELECT c.id FROM CreditDebitDocMapping c WHERE c.trialDebitDocumentId IN :trialDebitDocIds")
    List<Integer> findCreditDebitDocMappingIdsByTrialDebitDocIds(@Param("trialDebitDocIds") List<Integer> trialDebitDocIds);

    @Modifying
    @Transactional
    @Query("UPDATE CreditDebitDocMapping c SET c.trialDebitDocumentId = :newId WHERE c.id IN :creditDebitDocMappingIds")
    void updateTrialDebitDocumentIds(@Param("creditDebitDocMappingIds") List<Integer> creditDebitDocMappingIds, @Param("newId") Integer newId);

    @Query("SELECT t FROM CreditDebitDocMapping t WHERE t.debtDocId IS NOT NULL AND t.debtDocId <> 0 AND t.creditDocId In :creditDocIds")
    List<CreditDebitDocMapping> findByCreditDocIdAndDebtDocIdNotNull(@Param("creditDocIds") List<Integer> creditDocIds);

    @Query(value="SELECT DISTINCT trialdebitdocumentid FROM tbltcreditdebitmapping WHERE CREDITDOCID = :creditDocId",nativeQuery = true)
    List<Integer> findTrialDebitDocumentIdByCreditDocId(@Param("creditDocId") Integer creditDocId);

    @Query("SELECT c.creditDocId FROM CreditDebitDocMapping c WHERE c.withdrawId = :withdrawId")
    List<Integer> findCreditDocIdsByWithdrawId(@Param("withdrawId") Integer withdrawId);

    @Query("SELECT COALESCE(SUM(t.adjustedAmount),0) FROM CreditDebitDocMapping t WHERE t.creditDocId = :creditDocId")
    double getTotalAdjustedForCredit(@Param("creditDocId") Integer creditDocId);
}
