package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerChargeHistoryRepository extends JpaRepository<CustomerChargeHistory, Integer> {

    List<CustomerChargeHistory> findAllByCustPlanMapppingIdIn(List<Integer> custPlanMapppingId);

    List<CustomerChargeHistory> findAllByCustomerIdAndChargeIdIn(Integer customerId, List<Integer> chargeIds);
    List<CustomerChargeHistory> findAllByCustomerIdAndChargeIdInAndCustPlanMapppingIdIn(Integer customerId, List<Integer> chargeIds,List<Integer> custPlanIds);

    @Query(value = " SELECT * from tbltcustchargehistory  WHERE  cust_plan_mapping_id= :cust_plan_mapping_id", nativeQuery = true)
    List<CustomerChargeHistory> findAllChargesByCprId(@Param("cust_plan_mapping_id") Integer custPlanMappingId);

    @Query( " SELECT cst from CustomerChargeHistory  cst WHERE  cst.custPlanMapppingId in :custPlanIds and cst.chargeType=:chargeTypeNonrecurring ")
    List<CustomerChargeHistory> findAllCustChargeHistory(List<Integer> custPlanIds, String chargeTypeNonrecurring);

    Integer countAllByCustomerIdAndAndChargeType(Integer custId,String postPaidAdvance);
    Integer countAllByCustPlanMapppingIdInAndChargeType(List<Integer> custpackids,String postPaidAdvance);
    List<CustomerChargeHistory> findAllByCustomerIdAndChargeType(Integer custId,String postPaidAdvance);


    @Query("SELECT cst.id FROM CustomerChargeHistory cst " +
            "WHERE cst.custPlanMapppingId IN " +
            "(SELECT cp.id FROM CustPlanMappping cp " +
            "WHERE cp.debitdocid = :debitDocId)")
    List<Integer> findChargeIds(@Param("debitDocId") Long debitDocId);

    List<CustomerChargeHistory> findAllByCustomerId(Integer custId);

    @Query(value = "SELECT next_charge_billdate from tbltcustchargehistory where cust_id= :childCustId order by next_charge_billdate ASC LIMIT 1", nativeQuery = true)
    LocalDateTime findNearestNextBillDate(@Param("childCustId")Integer childCustId);
}
