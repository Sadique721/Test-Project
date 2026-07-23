package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;


@Repository
public interface InventorySpecificationHistoryRepo extends JpaRepository<InventorySpecificationHistory,Long>, QuerydslPredicateExecutor<InventorySpecificationHistory> {
    List<InventorySpecificationHistory> findAllByItemIdAndStatus(Long itemId, String status);
    InventorySpecificationHistory findAllByItemIdAndParamIdAndParamValueAndStatus(Long itemId, Long paramId, String paramValue, String status);
    List<InventorySpecificationHistory> findAllByItemIdAndParamId(Long itemId, Long paramId);

    @Query(value = "SELECT * FROM tblhinventoryspecificationhistory WHERE itemid = :itemId ORDER BY createdate DESC LIMIT 1", nativeQuery = true)
    Optional<InventorySpecificationHistory> findLatestByItemId(@Param("itemId") Long itemId);

    Optional<InventorySpecificationHistory> findById(Long id);
}
