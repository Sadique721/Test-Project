package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DebitDocDetailRepository extends JpaRepository<DebitDocDetails, Integer> {

    List<DebitDocDetails> findAllByDebitdocumentid(Integer debitdocId);

//    @Query(value ="select t.chargetype from DebitDocDetails t where t.debitdocumentid =:debitdocId ORDER BY t.debitdocdetailid DESC LIMIT 1" )
//    String findByDebitdocumentid(Integer debitdocId);

    @Query(value ="SELECT t.chargetype FROM tbltdebitdocumentdetail t WHERE t.debitdocumentid = :debitdocId ORDER BY t.debitdocdetailid DESC LIMIT 1", nativeQuery = true)
    String findByDebitdocumentid(@Param("debitdocId") Integer debitdocId);


    @Modifying
    @Transactional
    @Query("UPDATE DebitDocDetails d SET d.mvnodebitdocumentid = :mvnodebitdocumentid WHERE d.debitdocdetailid IN :ids")
    void updateMvNodeBitDocumentId(@Param("mvnodebitdocumentid") Integer mvnodebitdocumentid, @Param("ids") List<Integer> ids);

    @Query("SELECT d.debitdocumentid FROM DebitDocDetails d WHERE d.debitdocdetailid IN :ids")
    List<Integer> findDebitDocIdByDebitDocDetailsIds(@Param("ids") List<Integer> debitDocDetailsId);

    @Query("SELECT d.debitdocumentid FROM DebitDocDetails d WHERE d.mvnodebitdocumentid= :debitDocId")
    List<Integer> findDebitDocIdByMvnoDebitDocId(@Param("debitDocId") Integer debitDocId);

    @Query("SELECT d FROM DebitDocDetails d WHERE d.mvnodebitdocumentid= :debitDocId")
    List<DebitDocDetails> findDebitDocIdAndServiceIdByMvnoDebitDocId(@Param("debitDocId") Integer debitDocId);
    @Query("SELECT d.debitdocdetailid FROM DebitDocDetails d WHERE d.mvnodebitdocumentid= :debitDocId")
    List<Integer> findDebitDocDetailsIdsByMvnoDebitDocId(@Param("debitDocId") Integer debitDocId);


    @Query("SELECT COUNT(d) FROM DebitDocDetails d WHERE d.mvnodebitdocumentid=  :mvnodebitdocumentid")
    Long countByMvnoDebitDocumentId(@Param("mvnodebitdocumentid") Integer mvnodebitdocumentid);

    @Query(value ="SELECT t.debitdocdetailid FROM tbltdebitdocumentdetail t WHERE t.debitdocumentid IN (:debitDocumentIds) AND t.mvnodebitdocumentid IS NOT NULL", nativeQuery = true)
    List<Integer> findAllDebitDocDetailIdsByDebitDocIds(@Param("debitDocumentIds") List<Integer> debitDocumentIds);
}
