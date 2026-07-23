package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository;

import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMgmtDTOLight;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingMgmtRepository extends JpaRepository<BuildingManagement, Long>, QuerydslPredicateExecutor<BuildingManagement> {


    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMgmtDTOLight(b.buildingMgmtId, b.buildingName) FROM BuildingManagement b WHERE b.areaId = :areaid")
    List<BuildingMgmtDTOLight> findBuildingMgmtByAreaId(@Param("areaid") Integer areaid);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMgmtDTOLight(b.buildingMgmtId, b.buildingName) FROM BuildingManagement b WHERE b.subAreaId = :subareaid")
    List<BuildingMgmtDTOLight> findBuildingMgmtBySubAreaId(@Param("subareaid") Integer subareaid);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMgmtDTOLight(b.buildingMgmtId, b.buildingName) FROM BuildingManagement b WHERE b.pincodeId = :pincodeid")
    List<BuildingMgmtDTOLight> findBuildingMgmtByPincodeId(@Param("pincodeid") Integer pincodeid);


    @Query("SELECT d FROM BuildingManagement d " + "WHERE d.isDeleted = false AND " + "(d.mvnoId = 1 OR d.mvnoId = :mvnoId OR :mvnoId = 1)")
    Page<BuildingManagement> findAllWithPagination(@Param("mvnoId") Integer mvnoId, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement(a.buildingMgmtId, a.buildingName, a.mvnoId, a.buildingType) " + "FROM BuildingManagement a WHERE a.isDeleted = false AND a.mvnoId IN (:mvnoIds)")
    Page<BuildingManagement> findAllByMvnoIds(@org.springframework.data.repository.query.Param("mvnoIds") List mvnoIds, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement(a.buildingMgmtId, a.buildingName, a.pincodeId, a.areaId, a.subAreaId, a.buildingType, a.mvnoId) " + "FROM BuildingManagement a WHERE a.id = :id")
    BuildingManagement findBuildingManagementById(Long id);

    @Query("SELECT COUNT(b) > 0 FROM BuildingManagement b WHERE b.subAreaId = :subAreaId AND b.isDeleted = false")
    boolean existsActiveBySubAreaId(@Param("subAreaId") Integer subAreaId);



    @Modifying
    @Query("UPDATE BuildingManagement b SET b.buildingName = :newName WHERE b.subAreaId = :subAreaId AND b.isDeleted = false")
    int updateBuildingNameBySubAreaId(@Param("subAreaId") Integer subAreaId, @Param("newName") String newName);
}
