package com.savbill.ticketmanagement.core.modules.Plan.repository;



import com.savbill.ticketmanagement.core.modules.Plan.domain.PostpaidPlan;

//import javafx.geometry.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostpaidPlanRepo extends JpaRepository<PostpaidPlan, Integer>, QuerydslPredicateExecutor<PostpaidPlan> {

//    @Query(value = "select * from TBLMPOSTPAIDPLAN where lower(name) like '%' :search  '%' order by POSTPAIDPLANID AND  MVNOID= :MVNOID OR MVNOID IS NULL",
//            countQuery = "select count(*) from TBLMPOSTPAIDPLAN where lower(name) like '%' :search '%' AND  MVNOID= :MVNOID OR MVNOID IS NULL",
//            nativeQuery = true)
//    Page<PostpaidPlan> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);
//
//    List<PostpaidPlan> findByStatusAndPlantype(String status, String planType);
//
//    List<PostpaidPlan> findAllByStatusAndPlantypeAndServiceIdAndPlanGroup(String status, String planType, Integer serviceId, String planGroup);
//
//    @Query("SELECT plan.name FROM PostpaidPlan plan where plan.id = :id")
//    String findNameById(@Param("id") Integer id);
//
//    @Query("select t from PostpaidPlan t where t.isDelete=false")
//    List<PostpaidPlan> findAll();
//
//    @Query(nativeQuery = true, value = "select * from tblmpostpaidplan t where t.is_delete = 0", countQuery = "select count(*) from tblmpostpaidplan t where t.is_delete = 0")
//    Page<PostpaidPlan> findAll(Pageable pageable);
//
//    @Query(nativeQuery = true, value = "select * from tblmpostpaidplan t where t.is_delete = 0 and MVNOID in :mvnoIds", countQuery = "select count(*) from tblmpostpaidplan t where t.is_delete = 0 and MVNOID in :mvnoIds")
//    Page<PostpaidPlan> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);
//
//
//    @Query("update PostpaidPlan b set b.isDelete=true where b.id=:id")
//    @Modifying
//    void deleteById(@Param("id") Integer id);
//
//    @Query(value = "select count(*) from tblcustpackagerel t where t.planid=:id", nativeQuery = true)
//    Integer deleteverified(@Param("id") Integer id);
//
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false and m.MVNOID in :mvnoIds ", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false and m.MVNOID in :mvnoIds ", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds")List mvnoIds);
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name);
//
//    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);
//
//    @Query(value = "select CREATEDBYSTAFFID from tblmpostpaidplan where NAME=:name and is_delete=false and MVNOID=:mvnoId and BUID in :buIds", nativeQuery = true)
//  	Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select CREATEDBYSTAFFID from tblmpostpaidplan where NAME=:name and is_delete=false and MVNOID=:mvnoId", nativeQuery = true)
//    Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId);
//
//    List<PostpaidPlan> findAllByServiceIdAndStatus(Integer serviceId, String Status);
//
//    List<PostpaidPlan> findPostpaidPlanByServiceId(Integer id);
//
//    List<PostpaidPlan> findAllByIsDeleteIsFalseOrderByIdDesc();
//
//
//    @Query(value = "select count(*) from tblcustomercafassignment t where t.staff_id =:s1", nativeQuery = true)
//    Long findMinimumApprovalReuqestForPlanByStaff(@Param("s1") Integer staffId);
//
//    Optional<PostpaidPlan> findById(@Param("postPaidPlanId") Integer postPaidPlanId);
//
////    @Query(value = "select POSTPAIDPLANID, NAME from TBLMPOSTPAIDPLAN t where t.POSTPAIDPLANID IN (:ids)", nativeQuery = true)
//    List<PostpaidPlan> findAllByIdIn(List<Integer> ids);
//
//    Boolean existsByIdAndServiceId(List<Integer> ids,Integer serviceId );
//
//    List<PostpaidPlan> findAllByStatusAndIsDeleteFalseAndIdIn(String active, List<Integer> leadplanIds);
//
//    @Query("SELECT plan.serviceId FROM PostpaidPlan plan where plan.id in :ids")
//    List<Long> findServiceIdByPlanId(@Param("ids") Set<Integer> ids);

      @Query(value = "select t.NAME from tblmpostpaidplan t" , nativeQuery = true)
      List<String> getAllPostpaidPlanName();

      @Query("SELECT plan.serviceId from PostpaidPlan plan where plan.name in :names")
      List<Long> findServiceIdByPlanName(@Param("names") List<String> names);

      @Query("SELECT plan.name FROM PostpaidPlan plan where plan.id in :planIds")
      List<String> getAllPostpaidPlanNameByIdIn(@Param("planIds") List<Integer> planIds);
}

