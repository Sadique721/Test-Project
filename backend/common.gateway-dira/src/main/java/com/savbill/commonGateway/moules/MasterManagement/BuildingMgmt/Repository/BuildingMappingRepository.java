package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository;

import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BuildingMappingRepository extends JpaRepository<BuildingMapping,Long>, QuerydslPredicateExecutor<BuildingMapping> {

    @Modifying
    @Transactional
    @Query("DELETE FROM BuildingMapping bm WHERE bm.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM tblmbuildingmapping t WHERE t.building_mgmt_id = :id AND (:listIsEmpty = true OR t.building_number NOT IN (:usedBuildingIds)) ORDER BY t.id ASC;", nativeQuery = true)
    List<BuildingMapping> findAllByBuildingManagementId(@Param("id") Long id,@Param("usedBuildingIds") List<String> usedBuildingIds, @Param("listIsEmpty") boolean listIsEmpty);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingMapping(a.id, a.buildingNumber) " +
            "FROM BuildingMapping a WHERE a.buildingManagement.buildingMgmtId = :buildingId and a.isDeleted = false")
    List<BuildingMapping> findAllByBuildingManagementId(Long buildingId);

}
