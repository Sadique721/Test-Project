//package com.savbill.revenuemanagement.productmanagement.Plan.repository;
//
//
//import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
//import com.savbill.revenuemanagement.core.entity.customers.Customers;
//import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Collection;
//import java.util.List;
//
//@Repository
//public interface CustPlanMappingRepository extends JpaRepository<CustPlanMappping, Long> {
////
////    List<CustPlanMappping> findAllByDebitdocid(long longValue);
////
////    List<CustPlanMappping> findAllByCustServiceMappingIdIn(List<Integer> iDs);
////
////    List<CustPlanMappping> findAllByCustomerIsAndPlanGroupInAndIsHold(Customers customers, List<PlanGroup> planGroup, Boolean isHold);
////
////    @Query("select cpm from CustPlanMappping cpm where cpm.custServiceMappingId in (:Ids)")
////    List<CustPlanMappping> getDebitDocIdByCustServiceMappingIdInCprIds(@Param("Ids") List<Integer> Ids);
//
//}
//
