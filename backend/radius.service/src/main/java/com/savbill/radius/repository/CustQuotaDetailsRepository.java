package com.savbill.radius.repository;


import com.savbill.radius.SoapApi.Dto.GetBalanceDto;
import com.savbill.radius.SoapApi.Dto.MeteredVolumeUsageDTO;
import com.savbill.radius.entity.CustPlanMappping;
import com.savbill.radius.entity.CustQuotaDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustQuotaDetailsRepository extends JpaRepository<CustQuotaDetails, Integer> {

    List<CustQuotaDetails> findAllByCustid(Integer custid);

    List<CustQuotaDetails> findAllByCustidOrderByIdDesc(Integer custid);


    CustQuotaDetails findByCustPlanMappping(CustPlanMappping custPlanMappping);

    CustQuotaDetails findByCustPlanMapppingId(Long cprId);

    //    @Query("select t.id+1  from CustQuotaDetails t order by 1 desc limit 1 ")
//    Integer getNextQuotaId();
    @Query(value = "select quotadtlsid+1  from tblcustquotadtls t order by 1 desc limit 1 ", nativeQuery = true)
    Integer getNextQuotaId();


    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetBalanceDto(t.id,t.planId ,t.totalQuota, t.usedQuota, t.currentSessionUsageVolume, t.usageQuotaType, cpm.service, t.quotaUnit, pp.name, pp.planGroup, t.currentSessionUsageTime, t.custPlanMappping.id) " +
            "FROM CustQuotaDetails t join CustPlanMappping cpm on t.custPlanMappping.id = cpm.id join PostpaidPlan pp on cpm.planId = pp.id " +
            "WHERE t.custid = :custid and cpm.custPlanStatus = 'Active' AND cpm.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY t.id DESC")
    List<GetBalanceDto> findByCustomerId(@Param("custid") int custid);


    @Query("SELECT q FROM CustQuotaDetails q WHERE q.custid = :custid")
    List<CustQuotaDetails> findByCustid(@Param("custid") Integer custid);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.MeteredVolumeUsageDTO(t.id,t.planId, t.totalQuota, t.usedQuota, t.currentSessionUsageVolume) " +
            "FROM CustQuotaDetails t " +
            "WHERE t.custid = :custid " +
            "ORDER BY t.id DESC")
    List<MeteredVolumeUsageDTO> getMeteredVolumeUsage(@Param("custid") int custid);

    //    CustQuotaDetails findByPlanId(Long planId);
    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetBalanceDto(t.id,t.planId ,t.totalQuota, t.usedQuota, t.currentSessionUsageVolume, t.usageQuotaType, cpm.service, t.quotaUnit, pp.name, pp.planGroup, t.currentSessionUsageTime) " +
            "FROM CustQuotaDetails t join CustPlanMappping cpm on t.custPlanMappping.id = cpm.id join PostpaidPlan pp on cpm.planId = pp.id " +
            "WHERE t.planId = :planId and t.custid =:custId  and cpm.custPlanStatus = 'Active' " +
            "ORDER BY t.id DESC")
    GetBalanceDto findByPlanId(@Param("planId") Long planId, @Param("custId") Integer custId);

    @Query(value = "SELECT new com.savbill.radius.SoapApi.Dto.GetBalanceDto(t.id,t.planId ,t.totalQuota, t.usedQuota, t.currentSessionUsageVolume, t.usageQuotaType, cpm.service, t.quotaUnit, pp.name, pp.planGroup, t.currentSessionUsageTime) " +
            "FROM CustQuotaDetails t join CustPlanMappping cpm on t.custPlanMappping.id = cpm.id join PostpaidPlan pp on cpm.planId = pp.id " +
            "WHERE t.custid = :custid and cpm.custPlanStatus = 'Active' AND cpm.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY t.id DESC")
    List<GetBalanceDto> findBySubscriberID(@Param("custid") int custid);
//    @Query("SELECT t FROM CustQuotaDetails t join CustPlanMappping cpm on t.custPlanMappping.id = cpm.id join PostpaidPlan pp on cpm.planId = pp.id " +
//            "WHERE t.custid = :custid and cpm.custPlanStatus = 'Active' " +
//            "ORDER BY t.id DESC",nativeQuery = true)

    @Query(value = "select * from tblcustquotadtls t " +
            "        join  tblcustpackagerel cpm on t.custpackageid = cpm.custpackageid " +
            "        join tblmpostpaidplan pp on cpm.planid = pp.POSTPAIDPLANID " +
            "        where t.custid = :custid and cpm.cust_plan_status ='Active' AND cpm.endDate > CURRENT_TIMESTAMP", nativeQuery = true)
    List<CustQuotaDetails> findAllByCustidAndCustStutusActive(Integer custid);


    @Query("SELECT c.planId, c.totalQuota, c.usedQuota, c.currentSessionUsageVolume, c.usageQuotaType, c.quotaUnit, " +
            "c.custPlanMappping " +
            "FROM CustQuotaDetails c WHERE c.custid = :custid AND c.isDelete = false AND c.custPlanMappping.custPlanStatus = 'Active'")
    List<Object[]> findCustQuotaDetailsByCustidAndStatus(@Param("custid") Integer custid);

    @Query(value = "SELECT * FROM tblcustquotadtls t " +
            "join tblcustpackagerel cpm on t.custpackageid = cpm.custpackageid " +
            "WHERE t.custid = :custid and cpm.purchase_type not in ('Volume Booster','Bandwidthbooster') and cpm.cust_plan_status = 'Active'", nativeQuery = true)
    List<CustQuotaDetails> findBasePlanQuotaByCustid(@Param("custid") Integer custid);

    @Query("select cq from CustQuotaDetails cq where cq.custPlanMappping.id in :ids")
    List<CustQuotaDetails> findAllByCprIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE CustQuotaDetails cq SET cq.totalQuota = :totalQuota, cq.quotaUnit = :quotaUnit, cq.quotaType = :quotaType, cq.usageQuotaType = :usageQuotaType WHERE cq.custPlanMappping.id IN :cprIds")
    int updateQuotaDetailsByCprIds(@Param("totalQuota") Double totalQuota, @Param("quotaUnit") String quotaUnit, @Param("quotaType") String quotaType, @Param("usageQuotaType") String usageQuotaType, @Param("cprIds") List<Long> cprIds);


    @Query("SELECT p.name, m.custPlanStatus, p.startDate, p.endDate, m.purchaseType, " +
            "c.totalQuota, c.usedQuota, c.currentSessionUsageVolume " +
            "FROM CustQuotaDetails c " +
            "JOIN c.custPlanMappping m " +
            "JOIN PostpaidPlan p ON p.id = m.planId " +
            "WHERE c.custid = :custid " +
            "AND m.custPlanStatus = 'Active' " +
            "AND c.planId = m.planId")
    List<Object[]> getCustomerPlanDetails(@Param("custid") Integer custid);


}
