package com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemAssignHistoryMappingRepo extends JpaRepository<ItemAssignHistoryMapping,Long>, QuerydslPredicateExecutor<ItemAssignHistoryMapping> {


    @Query(value = "SELECT * FROM tbltitemassignhistorymapping WHERE itemid = :itemId ORDER BY createdate DESC LIMIT 1", nativeQuery = true)
    Optional<ItemAssignHistoryMapping> findLatestByItemId(@Param("itemId") Long itemId);

    List<ItemAssignHistoryMapping> findAllByItemId(Long itemId);
}
