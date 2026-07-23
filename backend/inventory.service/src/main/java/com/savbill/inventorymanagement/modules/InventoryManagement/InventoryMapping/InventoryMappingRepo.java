package com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMappingRepo extends JpaRepository<InventoryMapping, Long>, QuerydslPredicateExecutor<InventoryMapping> {
    List<InventoryMapping> findAllByIsDeletedIsFalse();

    @Query("SELECT ownerType FROM InventoryMapping WHERE id = :id AND isDeleted = false")
    String findOwnerTypeById(@Param("id") Long id);

    @Query("SELECT ownerId FROM InventoryMapping WHERE id = :id AND isDeleted = false")
    Long findOwnerIdById(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM tbltinventorymapping im " +
            "WHERE im.owner_id = :ownerId " +
            "AND LOWER(im.owner_type) = LOWER(:ownerType) " +
            "AND im.product_id = :productId " +
            "AND im.is_deleted = FALSE",
            nativeQuery = true)
    Integer countMappingByOwnerIdAndTypeAndProductId(
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("productId") Long productId
    );

    @Query(value = "SELECT COUNT(*) FROM tbltinventorymapping im " +
            "WHERE im.owner_id = :ownerId " +
            "AND LOWER(im.owner_type) = LOWER(:ownerType) " +
            "AND im.product_id = :productId " +
            "AND im.is_deleted = FALSE " +
            "AND im.mvno_id IN (:mvnoIds) ",
            nativeQuery = true)
    Integer countMappingByOwnerIdAndTypeAndProductIdAndMvnoIds(
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("productId") Long productId,
            @Param("mvnoIds") List<Integer> mvnoIds
    );

    @Query("SELECT im.id FROM InventoryMapping im WHERE im.ownerId = :ownerId AND LOWER(im.ownerType) = 'pop' AND im.isDeleted = false")
    List<Long> findIdsByOwnerIdAndPOPType(@Param("ownerId") Long ownerId);

    @Query("SELECT im.id FROM InventoryMapping im WHERE im.ownerId = :ownerId AND LOWER(im.ownerType) = 'pop' AND im.isDeleted = false AND im.mvnoId IN (:mvnoId)")
    List<Long> findIdsByOwnerIdAndPOPTypeAndMvnoId(@Param("ownerId") Long ownerId, @Param("mvnoId") List<Integer> mvnoId);

}
