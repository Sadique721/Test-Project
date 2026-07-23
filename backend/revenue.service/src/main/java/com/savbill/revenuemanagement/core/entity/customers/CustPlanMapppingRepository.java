package com.savbill.revenuemanagement.core.entity.customers;

import com.savbill.revenuemanagement.core.dto.invoice.CustPlanMappingPartial;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface CustPlanMapppingRepository extends JpaRepository<CustPlanMappping, Integer> {

    @Query(value = "select t.discount from CustPlanMappping t where t.id =:cprId")
    Double findDiscountById(Integer cprId);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.custServiceMappingId in (:custServiceMappingId) and (cpm.custPlanStatus = 'STOP' or cpm.custPlanStatus = 'Terminate')")
    List<Integer> getAllByCustServiceMappingIdIn(@Param("custServiceMappingId") List<Integer> custServiceMappingId);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.custServiceMappingId in (:custServiceMappingId) and cpm.isInvoiceCreated = false ")
    List<Integer> getAllByCustServiceMappingIdInForCancelAndRegen(@Param("custServiceMappingId") List<Integer> custServiceMappingId);


    @Query("select csm.serviceId from CustomerServiceMapping csm inner join CustPlanMappping cpm on csm.id = cpm.custServiceMappingId where cpm.id =:custPlanMapId")
    Long getServiceIdFromCustPlanMappingId(Integer custPlanMapId);

    List<CustPlanMappping> findAllByCustRefIdIsNotNull();

    List<CustPlanMappping> findAllByIdIn(List<Integer> custpackIds);

    @Query(value = "select debitdocid from TBLCUSTPACKAGEREL where cust_ref_id in (:custRefId)", nativeQuery = true)
    List<Integer> findAllByCustRefId(Set<Integer> custRefId);


    List<CustPlanMappping> findAllByDebitdocid(Long id);

    @Query(value = "select * from savbillrevenuemanagement.TBLCUSTPACKAGEREL where custid=:id",nativeQuery = true)
    List<CustPlanMappping> findAllByCustomerId(@Param("id") Integer id);
    List<CustPlanMappping> findAllByCustomerIdIn(List<Integer> id);
    List<CustPlanMappping> findAllByCustPlanStatusAndIdIn(String stop, List<Integer> ids);

    @Query(value = "select * from savbillrevenuemanagement.TBLCUSTPACKAGEREL where custid=:custId and grace_days > 0 ",nativeQuery = true)
    List<CustPlanMappping> findAllByGraceDaysAndCustomerId(@Param("custId")Integer custId);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.debitdocid in (:Ids)")
    List<Integer> getAllByCustPlanMappingIdInDebitDocIds(@Param("Ids") List<Long> Ids);

    @Query("select cpm.custServiceMappingId from CustPlanMappping cpm where cpm.id in (:Ids)")
    List<Integer> getAllByCustServiceMappingIdInCprIds(@Param("Ids") List<Integer> Ids);

    List<CustPlanMappping> findAllByCustServiceMappingId(Integer customerServiceMappingId);


    List<CustPlanMappping> findAllByDebitdocid(long longValue);

    @Query("SELECT c.startDate AS startDate, c.id AS id " +
            "FROM CustPlanMappping c " +
            "WHERE c.debitdocid = :debitdocid " +
            "OR (:includeCustPackRelId = true AND c.id = :custpackrelid)")
    List<CustPlanMappingPartial> findRelevantMappings(@Param("debitdocid") Long debitdocid,
                                                      @Param("includeCustPackRelId") boolean includeCustPackRelId,
                                                      @Param("custpackrelid") Integer custpackrelid);


    List<CustPlanMappping> findAllByCustServiceMappingIdIn(List<Integer> iDs);

    List<CustPlanMappping> findAllByCustomerIsAndPlanGroupInAndIsHold(Customers customers, List<PlanGroup> planGroup, Boolean isHold);

    @Query("select cpm from CustPlanMappping cpm where cpm.custServiceMappingId in (:Ids)")
    List<CustPlanMappping> getDebitDocIdByCustServiceMappingIdInCprIds(@Param("Ids") List<Integer> Ids);
    @Query(value = "select * from TBLCUSTPACKAGEREL where debitdocid= :debitDocId and cust_plan_status= :status", nativeQuery = true)
    List<CustPlanMappping> findAllByDebitdocidAndCustPlanStatus(@Param("debitDocId") Integer debitDocId, String status);

    @Query(value = "select * from TBLCUSTPACKAGEREL where cust_cpr in :custCprs", nativeQuery = true)
    List<CustPlanMappping> getAllCustPlanMappingByCustCPRList(@Param("custCprs") List<Integer> custCprs);

    List<CustPlanMappping> findAllByPlanGroupIn(List<PlanGroup> planGroups);

    @Query("select cpm from CustPlanMappping cpm where cpm.debitdocid =:longValue")
    List<CustPlanMappping> findAllByDebitdocumentid(Long longValue);

    List<CustPlanMappping> findAllByRenewalId(Integer renewalId);

    Integer countAllByCustomerAndIstrialplan(Customers customers,boolean isTrialPlan);

    @Query(value = "select custpackageid from TBLCUSTPACKAGEREL where custpackageid in :custCprs and cust_plan_status= :status", nativeQuery = true)
    List<Integer> getAllCustPlanMappingByCustCPRListAndStatus(@Param("custCprs") Set<Integer> custCprs,String status);

    List<CustPlanMappping> findAllByCustomerIdAndEndDate(Integer id, LocalDateTime enddate);

    @Query(value = "select debitdocid from TBLCUSTPACKAGEREL where custpackageid in :custCprs ", nativeQuery = true)
    List<Integer> getAllDebitIdsByCustCPRList(@Param("custCprs") List<Integer> custCprs);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.custServiceMappingId in (:Ids)")
    List<Integer> getIdByCustServiceMappingIdIn(@Param("Ids") List<Integer> Ids);

    @Query(value = "select custpackageid from TBLCUSTPACKAGEREL where custid = :custId and cust_plan_status= :status", nativeQuery = true)
    List<Integer> findCustpackIdsBycustIdsAndStatus(@Param("custId") Integer custId,String status);

    @Query(value = "select planid from TBLCUSTPACKAGEREL where custpackageid in :custCprs", nativeQuery = true)
    List<Integer> getAllPlansByCustCPRList(@Param("custCprs") List<Integer> custCprs);
    @Query("SELECT c.expiryDate FROM CustPlanMappping c WHERE c.customer.id = :custId ORDER BY c.id DESC")
    LocalDateTime findLatestExpiryDateByCustId(@Param("custId") Integer custId);
    @Query("SELECT c.expiryDate FROM CustPlanMappping c WHERE c.customer.id = :custId ORDER BY c.id DESC")
    List<LocalDateTime> findLatestExpiryDatesByCustId(@Param("custId") Integer custId);


    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c WHERE c.custid = :custId AND c.startdate > CURRENT_TIMESTAMP AND c.purchase_type IN :purchaseTypes", nativeQuery = true)
    List<CustPlanMappping> findFuturePlansByCustomerId(@Param("custId") Integer custId, @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c WHERE c.custid = :custId AND c.startdate <= CURRENT_TIMESTAMP AND c.expirydate >= CURRENT_TIMESTAMP AND c.purchase_type IN :purchaseTypes", nativeQuery = true)
    List<CustPlanMappping> findActivePlanListByCustomerId(@Param("custId") Integer custId, @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c WHERE c.custid = :custId AND CURRENT_TIMESTAMP > c.enddate AND c.purchase_type IN :purchaseTypes", nativeQuery = true)
    List<CustPlanMappping> findExpirePlanListByCustomerId(@Param("custId") Integer custId, @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.startdate, p.offerprice, c.expirydate " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.startdate <= CURRENT_TIMESTAMP " +
            "AND c.expirydate >= CURRENT_TIMESTAMP " +
            "AND c.purchase_type IN :purchaseTypes", nativeQuery = true)
    List<Object[]> findActivePlanDetails(@feign.Param("custId") Integer custId,
                                         @feign.Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.startdate, p.offerprice, c.expirydate " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.purchase_type IN :purchaseTypes " +
            "ORDER BY c.expirydate DESC " +
            "LIMIT 1",
            nativeQuery = true)
    List<Object[]> findLatestPlanDetails(@feign.Param("custId") Integer custId,
                                         @feign.Param("purchaseTypes") List<String> purchaseTypes);



    @Query(value = "select * from tblcustpackagerel t where t.custpackageid =:id", nativeQuery = true)
    CustPlanMappping findByCprId(@Param("id") Long id);

    @Query(value = "SELECT c.custid FROM tblcustomers c " +
            "JOIN tblcustpackagerel r ON c.custid = r.custid " +
            "WHERE r.renewal_for_booster = 1", nativeQuery = true)
    List<Integer> findCustomersWhereIsRenewalForBoosterFalse();


    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c " +
            "WHERE c.custid IN :custIds " +
            "AND c.offer_price > 0 "+
            "AND c.renewal_for_booster = 1 " +
            "AND NOT ( " +
            "     (c.startdate <= CURRENT_TIMESTAMP AND c.expirydate >= CURRENT_TIMESTAMP) " + // active
            ")", nativeQuery = true)
    List<CustPlanMappping> findOtherPlansByCustomerIds(
            @Param("custIds") List<Integer> custIds
    );


    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c WHERE c.custid IN :custIds", nativeQuery = true)
    List<CustPlanMappping> findByCustomerIds(@Param("custIds") List<Integer> custIds);

    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping c SET c.renewalForBooster = :renewalForBooster WHERE c.id = :custPlanMappingId")
    int updateRenewalForBoosterById(@feign.Param("renewalForBooster") Boolean renewalForBooster,
                                    @feign.Param("custPlanMappingId") Integer custPlanMappingId);

    @Query("SELECT c.startDate as startDate, c.id as id, c.debitdocid as debitDocId, c.custServiceMappingId as custPackRelId " +
            "FROM CustPlanMappping c " +
            "WHERE c.debitdocid IN :docIds")
    List<CustPlanMappingPartial> findRelevantMappingsForDocs(@Param("docIds") List<Long> docIds);

    @Query(value = "select t.enddate from TBLCUSTPACKAGEREL t where t.custid = :custId order by t.custpackageid desc limit 1", nativeQuery = true)
    LocalDateTime findLatestEndDateByCustId(@Param("custId") Integer custId);

}
