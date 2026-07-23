package com.savbill.revenuemanagement.core.repository.customer;


import com.savbill.revenuemanagement.core.entity.customers.CustChargeDetails;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustChargeRepository extends JpaRepository<CustChargeDetails, Integer> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

//	List<CustomerLedger> findByCustomer(Customers customer);

    List<CustChargeDetails> findByChargetypeAndCustomer(String chargtype, Customers cust);

    List<CustChargeDetails> findByCustomer(Customers cust);

    @Query(nativeQuery = true, value = "select * from tblcustchargedtls t \n" +
            "inner join tblcharges t2 on t2.CHARGEID = t.chargeid \n" +
            "where t.custid = :s1 and t2.chargecategory != 'IP' and t.is_reversed = 0 \n")
    List<CustChargeDetails> findByCustomer_Id(@Param("s1") Integer id);

    //    List<CustChargeDetails> findAllByCustomer_IdAndChargeidAndIsUsedIsFalse(Integer customers,Integer chargeId);
//    List<CustChargeDetails> findAllByCustomer_IdAndChargeidAndIsUsedIsTrue(Integer customers,Integer chargeId);
    @Query(value = "select * from tblcustchargedtls t where t.purchase_entity_id=:purchesentityid AND t.chargetype=:chargetype", nativeQuery = true)
    CustChargeDetails findByPurchaseEntityIdAndChargetype(@Param("purchesentityid") Long purchesentityid, @Param("chargetype") String chargetype);

    @Query(value = "select * from tblcustchargedtls custcharges inner join tblcharges charges  on custcharges.chargeid = charges.CHARGEID and charges.chargecategory=:chargeCategory and custcharges.custid=:custId and custcharges.is_used=:isused and custcharges.is_reversed=:isreversed and charges.is_delete = false", nativeQuery = true)
    List<CustChargeDetails> getIpPurchasedCharge(@Param("custId") Long custId, @Param("chargeCategory") String chargeCategory, @Param("isused") Boolean isUsed, @Param("isreversed") Boolean isreversed);

    @Query(value = "select * from tblcustchargedtls custcharges inner join tblcharges charges  on custcharges.chargeid = charges.CHARGEID and charges.chargecategory=:chargeCategory and custcharges.custid=:custId and custcharges.is_reversed=:isreversed and charges.is_delete = false", nativeQuery = true)
    List<CustChargeDetails> getIpPurchasedChargeForRollback(@Param("custId") Long custId, @Param("chargeCategory") String chargeCategory, @Param("isreversed") Boolean isreversed);

	List<CustChargeDetails> findAllByCustomer(Customers customer);

    @Query(value = "select * from tblcustchargedtls t where t.custpackageid=:cprId", nativeQuery = true)
    List<CustChargeDetails> findByCprId(@Param("cprId") Integer cprId);

    List<CustChargeDetails> findAllByCustomerAndChargeidAndIsDeletedIsFalse(Customers customers, Integer chargeid);

    @Query(value = "select DISTINCT t.custid from tblcustchargedtls t where t.installment_enabled=true and t.installment_no < t.total_installments", nativeQuery = true)
    List<Integer> findAllCustomerWhereInstallmentEnabled();

    @Query(value = "select t.cstchargeid, t.CREATEDBYSTAFFID, t.createbyname, c.cstatus, c.customertype from tblcustchargedtls t join tblcustomers c on c.custid = t.custid  where t.custId = :custId and t.installment_no < t.total_installments and t.next_installment_date = CURRENT_DATE and t.installment_enabled = true", nativeQuery = true)
    List<Object[]> findCustChargeByCustId(@Param("custId") Integer custId);

}
