package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Repository
public interface InventorySpecificationRepo  extends JpaRepository<InventorySpecification,Long>, QuerydslPredicateExecutor<InventorySpecification> {
    InventorySpecification findBySpecificationParametersId(Long specificationParameters_id);
    List<InventorySpecification> findAllByInward_Id(Long inwardId);
    List<InventorySpecification> findAllByInvenSpecId(Long invenSpecId);
    InventorySpecification findAllBySpecificationParameters_IdAndInvenSpecId(Long specificationParameters_id, Long invenSpecId);
    List<InventorySpecification> findAllByIdIn(Collection<Long> id);

    @Query("SELECT invSpec FROM InventorySpecification invSpec WHERE invSpec.inward.id IN :ids")
    List<InventorySpecification> findAllByInwardIn(@Param("ids") Collection<Long> ids);
    @Query(value = "SELECT COUNT(*) FROM tblminventoryspecification WHERE inward_id = :id", nativeQuery = true)
    int countByInwardId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tblminventoryspecification WHERE inward_id = :inwardId", nativeQuery = true)
    void deleteByInwardId(@Param("inwardId") Long inwardId);

}
