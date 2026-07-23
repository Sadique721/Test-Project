package com.savbill.commonGateway.moules.MasterManagement.Branch.repository;


import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.BranchServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchServiceAreaMappingRepository extends JpaRepository<BranchServiceAreaMapping, Long>, QuerydslPredicateExecutor<BranchServiceAreaMapping> {

    List<BranchServiceAreaMapping> findAllByBranchId(Integer branchId);

    @Query(value = "SELECT t.branchid\n" +
            "FROM tbltbranchservicearearel t \n" +
            "JOIN tblmbranch t2 ON t.branchid = t2.branchid \n" +
            "WHERE t.servicearea_id IN (:serviceAreaIds) AND t2.MVNOID = :mvnoid", nativeQuery = true)
    List<Long> findAllByServiceareaIdIn(@Param("serviceAreaIds") List<Integer> serviceAreaIds,
                                                            @Param("mvnoid") Integer mvnoid);


    @Query(value = "select * from tbltbranchservicearearel t where servicearea_id =:serviceAreaid", nativeQuery = true)
    List<BranchServiceAreaMapping> findAllByServiceareaId(@Param("serviceAreaid") Long serviceAreaid);

    @Query(value = "select * from tbltbranchservicearearel t where servicearea_id in (:serviceAreaids)", nativeQuery = true)
    List<BranchServiceAreaMapping> findAllByServiceareaId(@Param("serviceAreaids") List<Integer> serviceAreaids);

    @Query(value = "select servicearea_id \n" +
            "from tbltbranchservicearearel t \n" +
            "join tblmservicearea t2 \n" +
            "on t2.service_area_id =t.servicearea_id where t2.MVNOID <>1",nativeQuery = true)
    List<Integer> serviceAreaIdListWhereBranchIsNotBind();

    @Query(value  = "select branchServiceAreaMapping.serviceareaId\n" +
            "from BranchServiceAreaMapping branchServiceAreaMapping where branchId =:branchId")
    List<Integer> getAllServiceAreaIdsWithBranchId(@Param("branchId")Integer branchId);


    List<BranchServiceAreaMapping> findAllByBranchIdIn(List<Integer> branchIds);


     BranchServiceAreaMapping findBranchServiceAreaMappingByServiceareaId(Integer serviceAreaId);

    @Query("SELECT b.branchId FROM BranchServiceAreaMapping b WHERE b.serviceareaId IN :serviceAreaIds")
    List<Integer> findBranchIdsByServiceareaIdIn(@Param("serviceAreaIds") List<Integer> serviceAreaIds);



}
