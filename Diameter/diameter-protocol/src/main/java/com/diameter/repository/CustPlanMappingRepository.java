package com.diameter.repository;

import com.diameter.model.CustPlanMappping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustPlanMappingRepository extends JpaRepository<CustPlanMappping, Long> {
//    List<CustPlanMappping> findAllByCustid(Integer id);
//
//    List<CustPlanMappping> findAllByCustidAndPurchaseTypeAndCustPlanStatus(Integer id, String purchaseType, String status);
//
//    //
////    List<CustPlanMappping> findAllByCustomerIdAndPlanId(Integer customerId, Integer planId);
////
//    List<CustPlanMappping> findByPlanId(Integer planId);
//
//    CustPlanMappping findById(Integer planId);
//
//    @Query(value = "select * from tblcustpackagerel t where t.custpackageid =:id", nativeQuery = true)
//    CustPlanMappping findByCprId(@Param("id") Long id);
//
//    List<CustPlanMappping> findAllByPlanIdIn(List<Integer> planId);
////    @Query(value = "select t.id+1  from CustPlanMappping t order by 1 desc limit 1 ")
////    Long getNextCPRId();
//
//    @Query(value = "select custpackageid+1  from TBLCUSTPACKAGEREL t order by 1 desc limit 1 ", nativeQuery = true)
//    Long getNextCPRId();
//
//    List<CustPlanMappping> findAllByCustidAndCustPlanStatusAndPurchaseType(Integer id, String status, String purchaseType);
//
//    @Query("SELECT c.id, c.planId " +
//            "FROM CustPlanMappping c " +
//            "WHERE c.custid = :id AND c.custPlanStatus = :status AND c.purchaseType = :purchaseType")
//    List<Object[]> findAllByCustidAndCustPlanStatusAndPurchaseTypeNative(
//            @Param("id") Integer id,
//            @Param("status") String status,
//            @Param("purchaseType") String purchaseType);
//
//    @Query("select t from CustPlanMappping t join LiveUser l on t.id = l.cprId where t.planId in :ids")
//    List<CustPlanMappping> fetchCusPlanByIdsAndLiveUSer(@Param("ids") List<Integer> ids);
//
//    @Query("select t from CustPlanMappping t where t.planId in :ids and t.custPlanStatus = 'Active' and t.endDate > now()")
//    List<CustPlanMappping> fetchCusPlanByIdsAndStatus(@Param("ids") List<Integer> ids);
//
//    @Modifying
//    @Query("UPDATE CustPlanMappping t SET t.qospolicyid = :qosPolicyId WHERE t.planId = :planId AND t.custPlanStatus = 'Active' AND t.endDate > CURRENT_TIMESTAMP")
//    int updateQosPolicyIdByPlanId(@Param("qosPolicyId") String qosPolicyId, @Param("planId") int planId);
//
//
//    @Query("SELECT t.id FROM CustPlanMappping t WHERE t.planId = :planId AND t.custPlanStatus = 'Active' AND t.endDate > CURRENT_TIMESTAMP")
//    List<Long> fetchUpdatedCprIds(@Param("planId") int planId);
//
//
//    @Query(value = "SELECT cpm.enddate, cpm.startdate, cpm.custpackageid, cpm.cust_plan_status, " +
//            "pp.POSTPAIDPLANID, pp.NAME " +
//            "FROM TBLCUSTPACKAGEREL cpm " +
//            "JOIN TBLMPOSTPAIDPLAN pp ON cpm.planid = pp.POSTPAIDPLANID " +
//            "WHERE cpm.custid = :custid " +
//            "AND pp.plangroup = 'Volume Booster'",
//            nativeQuery = true)
//    List<Object[]> findTopUpSubscriptionsByCustomerId(@Param("custid") Integer customerId);
//
//    @Query(value = "SELECT cpm.enddate, cpm.startdate, cpm.custpackageid, cpm.cust_plan_status, " +
//            "pp.POSTPAIDPLANID, pp.NAME " +
//            "FROM TBLCUSTPACKAGEREL cpm " +
//            "JOIN TBLMPOSTPAIDPLAN pp ON cpm.planid = pp.POSTPAIDPLANID " +
//            "WHERE cpm.custid = :custid " +
//            "AND (pp.plangroup = 'Bandwidthbooster' OR pp.plangroup = 'Volume Booster') " +
//            "AND cpm.cust_plan_status = 'Active' " +
//            "AND cpm.enddate >= NOW()",  // Ensures proper timestamp comparison
//            nativeQuery = true)
//    List<Object[]> findAddOnSubscriptionsByCustomerId(@Param("custid") Integer customerId);
//
//    @Query(value = "select t2.DISPLAYNAME  from tblcustpackagerel t right join tblmpostpaidplan t2 on t.planid = t2.POSTPAIDPLANID where t.custpackageid = :cprId", nativeQuery = true)
//    String getPlanNameFromCPRID(@Param("cprId") Long cprId);

}
